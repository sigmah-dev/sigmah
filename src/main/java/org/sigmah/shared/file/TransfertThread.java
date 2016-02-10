package org.sigmah.shared.file;

/*
 * #%L
 * Sigmah
 * %%
 * Copyright (C) 2010 - 2016 URD
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import java.util.Date;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.fileapi.Blob;
import org.sigmah.offline.fileapi.Int8Array;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.shared.command.DownloadSlice;
import org.sigmah.shared.command.UploadSlice;
import org.sigmah.shared.command.result.VoidResult;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.FileType;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.shared.servlet.FileUploadResponse;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
class TransfertThread {
	private TransfertAsyncDAO transfertAsyncDAO;
	private DispatchAsync dispatcher;
	
	private Task task;
	private FileVersionDTO fileVersion;
	private JsArray<Int8Array> data;
	private int offset;
	private boolean online;
    private double speed;
	
	private Html5TransfertManager transfertManager;

	public void setDispatcher(DispatchAsync dispatcher) {
		this.dispatcher = dispatcher;
	}

	public void setTransfertAsyncDAO(TransfertAsyncDAO transfertAsyncDAO) {
		this.transfertAsyncDAO = transfertAsyncDAO;
	}

	public void setTransfertManager(Html5TransfertManager transfertManager) {
		this.transfertManager = transfertManager;
	}

	public void setTask(Task task) {
		this.task = task;
		
		if(task != null) {
			final TransfertJS transfertJS = task.getTransfert();
			this.fileVersion = transfertJS.getFileVersion().toDTO();
			this.offset = transfertJS.getProgress();
			this.data = transfertJS.getData();
		}
        
        start();
	}

	public Task getTask() {
		return task;
	}
	
	public boolean isAvailable() {
		return task == null;
	}
    
    public double getProgress() {
        return (double)offset / (double)fileVersion.getSize();
    }

    public double getSpeed() {
        return speed;
    }
	
    public void setOnline(boolean online) {
        this.online = online;
        start();
    }

	public Blob getBlob() {
		return Blob.createBlob(data, FileType.fromExtension(fileVersion.getExtension(), FileType._DEFAULT).getContentType());
	}
	
	public Int8Array getInt8Array() {
		return Int8Array.createInt8Array(data);
	}
	
	public void start() {
        if(task != null) {
            switch(task.getTransfert().getType()) {
                case DOWNLOAD:
					if(online) {
						Log.info("Début du téléchargement de '" + fileVersion.getName() + "'...");
						downloadNextSlice(Html5TransfertManager.BASE_SLICE_SIZE);
					}
                    break;

                case UPLOAD:
					if(online) {
						Log.info("Début de l'envoi de '" + fileVersion.getName() + "'...");
						uploadNextSlice(Html5TransfertManager.BASE_SLICE_SIZE);
					} else {
						fireLoad(false);
					}
                    break;
            }
        }
	}

	private void downloadNextSlice(final int size) {
		// If this thread is stopped, do nothing
		if(!online) {
			return;
		}
		
		final Date startDate = new Date();
		
		dispatcher.execute(new DownloadSlice(fileVersion, offset, size), new AsyncCallback<FileSlice>() {
			@Override
			public void onFailure(Throwable caught) {
				fireFailure(Cause.SERVER_ERROR);
			}

			@Override
			public void onSuccess(final FileSlice fileSlice) {
				final double timeInSeconds = (new Date().getTime() - startDate.getTime()) / 1000.0;
				speed = size / timeInSeconds;
				
				data.push(Int8Array.toInt8Array(fileSlice.getData()));
				offset = (int) Math.min(offset + size, fileVersion.getSize());
				
				fireProgress(getProgress(), speed);
				Log.info("'" + fileVersion.getName() + "' " + formatSize(offset) + " (" +  formatSize(speed) + ")");
                
				updateTransfertProgress(new AsyncCallback<TransfertJS>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.trace("An error occured while saving slice data of file '" + task.getTransfert().getFileVersion().getName() + "'.");
                    }

                    @Override
                    public void onSuccess(TransfertJS result) {
                        if(!fileSlice.isLast()) {
                            // Adapting speed to finish a slice every 2 seconds
                            downloadNextSlice((int) (speed * Html5TransfertManager.SLICE_TRANSFERT_TIME));

                        } else {
                            fireLoad(true);
                        }
                    }
                });
			}
		});
	}

	private void uploadNextSlice(final int size) {
		// If this thread is stopped, do nothing
		if(!online) {
			return;
		}
		
		// Reading file data
		final Int8Array fileData = data.get(0);
		final int actualSize = Math.min(fileData.length() - offset, size);
		final byte[] bytes = new byte[actualSize];
		
		for(int index = 0; index < actualSize; index++) {
			bytes[index] = fileData.get(offset + index);
		}
		
		final UploadSlice uploadSlice = new UploadSlice();
		uploadSlice.setData(bytes);
		uploadSlice.setFileVersionDTO(fileVersion);
		uploadSlice.setOffset(offset);
		uploadSlice.setLast(offset + actualSize == fileData.length());
		
		final Date startDate = new Date();
		dispatcher.execute(uploadSlice, new AsyncCallback<VoidResult>() {
			@Override
			public void onFailure(Throwable caught) {
				fireFailure(Cause.SERVER_ERROR);
			}

			@Override
			public void onSuccess(VoidResult result) {
				final double timeInSeconds = (new Date().getTime() - startDate.getTime()) / 1000.0;
				speed = size / timeInSeconds;

				offset = (int) Math.min(offset + actualSize, fileVersion.getSize());
				fireProgress(getProgress(), speed);
				Log.info("'" + fileVersion.getName() + "' " + formatSize(offset) + " (" +  formatSize(speed) + ")");
				
				updateTransfertProgress(new AsyncCallback<TransfertJS>() {

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.trace("An error occured while saving slice data of file '" + task.getTransfert().getFileVersion().getName() + "'.");
                    }

                    @Override
                    public void onSuccess(TransfertJS result) {
                        // Downloading the next slice.
                        if(!uploadSlice.isLast()) {
                            uploadNextSlice((int) (speed * Html5TransfertManager.SLICE_TRANSFERT_TIME));
                        } else {
                            fireLoad(true);
                        }
                    }
                });
			}
		});
	}
	
	private void updateTransfertProgress(AsyncCallback<TransfertJS> callback) {
		final TransfertJS transfertJS = task.getTransfert();
		transfertJS.setProgress(offset);
		transfertJS.setData(data);
		transfertAsyncDAO.saveOrUpdate(transfertJS, callback);
	}
	
	protected void fireFailure(Cause cause) {
		final Task failedTask = task;
		task = null;
		
		if(failedTask.hasListener()) {
			failedTask.getProgressListener().onFailure(cause);
		}
		transfertManager.onTransfertFailure(failedTask, this);
	}
	
	protected void fireProgress(double progress, double speed) {
		if(task.hasListener()) {
			task.getProgressListener().onProgress(progress, speed);
		}
        transfertManager.onProgress();
	}
	
	protected void fireLoad(boolean removeTransfert) {
		final Task doneTask = task;
		task = null;
		
		if(doneTask.hasListener()) {
			// BUGFIX #685 & #781: sending a serialized fileVersion on load.
			doneTask.getProgressListener().onLoad(FileUploadResponse.serialize(fileVersion, null));
		}
        
		if(removeTransfert) {
			// Removing the transfert
			transfertAsyncDAO.remove(doneTask.getTransfert().getId());
		}
		
        // Calling the manager
		switch(doneTask.getTransfert().getType()) {
			case DOWNLOAD:
				transfertManager.onDownloadComplete(fileVersion, getInt8Array(), doneTask.hasListener(), this);
				break;
				
			case UPLOAD:
				transfertManager.onUploadComplete(this);
				break;
		}
	}
	
	private static String formatSize(double size) {
		final String[] type = {" octets", " Ko", " Mo", " Go", " To"};
		int typeIndex = 0;
		while(size > 1024.0 && typeIndex < type.length - 1) {
			size /= 1024.0;
			typeIndex++;
		}
		final double value = ((int)(size * 100)) / 100.0;
		return Double.toString(value) + type[typeIndex];
	}
	
}
