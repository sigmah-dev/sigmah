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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.sigmah.client.dispatch.DispatchAsync;
import org.sigmah.client.i18n.I18N;
import org.sigmah.client.ui.notif.N10N;
import org.sigmah.client.ui.widget.form.ButtonFileUploadField;
import org.sigmah.offline.dao.FileDataAsyncDAO;
import org.sigmah.offline.dao.TransfertAsyncDAO;
import org.sigmah.offline.fileapi.ArrayBuffer;
import org.sigmah.offline.fileapi.Blob;
import org.sigmah.offline.fileapi.FileReader;
import org.sigmah.offline.fileapi.Int8Array;
import org.sigmah.offline.fileapi.LoadFileAdapter;
import org.sigmah.offline.js.FileDataJS;
import org.sigmah.offline.js.FileVersionJS;
import org.sigmah.offline.js.TransfertJS;
import org.sigmah.offline.js.Values;
import org.sigmah.offline.status.ApplicationState;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.dto.value.FileVersionDTO;
import org.sigmah.shared.util.FileType;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.sigmah.client.event.EventBus;
import org.sigmah.client.event.OfflineEvent;
import org.sigmah.client.event.handler.OfflineHandler;
import org.sigmah.offline.fileapi.Files;

/**
 * Transfert files by slicing them.
 * Files are stored inside IndexedDB to allow offline re-download and on-connect
 * upload.
 *
 * When uploading, files are stored before the transfert.
 *
 * When downloading, files are transfered, then stored inside IndexedDB and
 * finally actually downloaded by the client.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Html5TransfertManager implements TransfertManager, HasProgressListeners {
	
	/**
	 * Size in bytes of the first slice.
	 */
	static final int BASE_SLICE_SIZE = 100 * 1024;
	
	/**
	 * Time in seconds to send or receive one slice.
	 */
	static final double SLICE_TRANSFERT_TIME = 2.0;
	
	/**
	 * Maximum number of retries in case of download or upload failure.
	 */
	private static final int MAXIMUM_NUMBER_OF_RETRIES = 5;
	
	private final EventBus eventBus;
	
	private final List<Task> downloads;
	private final List<Task> uploads;
	private final int[] currentTasks;
	
	private final TransfertThread downloadThread;
	private final TransfertThread uploadThread;
	
	private final DispatchAsync dispatchAsync;
	private final FileDataAsyncDAO fileDataAsyncDAO;
	private final TransfertAsyncDAO transfertAsyncDAO;
	private final DirectTransfertManager directTransfertManager;
	
	private ApplicationState state;
    
    private final Map<TransfertType, ProgressListener> progressListeners;

	public Html5TransfertManager(DispatchAsync dispatchAsync, FileDataAsyncDAO fileDataAsyncDAO, TransfertAsyncDAO transfertAsyncDAO, EventBus eventBus, DirectTransfertManager directTransfertManager) {
		this.dispatchAsync = dispatchAsync;
		this.fileDataAsyncDAO = fileDataAsyncDAO;
		this.transfertAsyncDAO = transfertAsyncDAO;
		this.eventBus = eventBus;
		this.directTransfertManager = directTransfertManager;

		// Creating transfert threads.
		this.downloads = new ArrayList<Task>();
		this.uploads = new ArrayList<Task>();
		this.currentTasks = new int[]{-1, -1};

		downloadThread = createTransfertThread();
		uploadThread = createTransfertThread();

		this.progressListeners = new EnumMap<TransfertType, ProgressListener>(TransfertType.class);

		// Adding a connection status listener to start or stop threads
		listenToConnectionStatusChanges(eventBus);
	}

    // Transfert thread handling -----------------------------------------------
    
    private TransfertThread createTransfertThread() {
		final TransfertThread transfertThread = new TransfertThread();
		transfertThread.setTransfertManager(this);
		transfertThread.setDispatcher(dispatchAsync);
		transfertThread.setTransfertAsyncDAO(transfertAsyncDAO);
		
		return transfertThread;
    }
    
    private void listenToConnectionStatusChanges(EventBus eventBus) {
		eventBus.addHandler(OfflineEvent.getType(), new OfflineHandler() {

			@Override
			public void handleEvent(OfflineEvent event) {
				state = event.getState();
				downloadThread.setOnline(state == ApplicationState.ONLINE);
				uploadThread.setOnline(state == ApplicationState.ONLINE);
			}
		});
    }
    
    public void queueTransfert(TransfertJS transfertJS, ProgressListener listener) {
        queueTask(new Task(transfertJS, listener));
	}
    
    public void queueTask(Task task) {
		switch(task.getTransfert().getType()) {
			case DOWNLOAD:
				downloads.add(task);
				nextDownload();
				break;
				
			case UPLOAD:
				uploads.add(task);
				nextUpload();
				break;
		}
	}
	
	public void nextDownload() {
		nextTask(TransfertType.DOWNLOAD, downloadThread, downloads);
	}
	
	public void nextUpload() {
		nextTask(TransfertType.UPLOAD, uploadThread, uploads);
	}
	
	private void nextTask(TransfertType type, TransfertThread thread, List<Task> tasks) {
		if(thread.isAvailable()) {
			currentTasks[type.ordinal()]++;
			
			if(currentTasks[type.ordinal()] < tasks.size()) {
				final Task task = tasks.get(currentTasks[type.ordinal()]);
				thread.setTask(task);
				
			} else if(currentTasks[type.ordinal()] == tasks.size()) {
				currentTasks[type.ordinal()] = -1;
				tasks.clear();
			}
		}
	}
	
	public void onTransfertFailure(final Task task, TransfertThread transfertThread) {
		if(task.getTries() < MAXIMUM_NUMBER_OF_RETRIES) {
			task.setTries(task.getTries() + 1);

			final Timer timer = new Timer() {
				@Override
				public void run() {
					queueTask(task);
				}
			};
			timer.schedule(3000 * task.getTries());
		}
        
		switch(task.getTransfert().getType()) {
			case DOWNLOAD:
				nextDownload();
				break;
				
			case UPLOAD:
				nextUpload();
				break;
		}
	}
	
    // Downloads ---------------------------------------------------------------
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void download(final FileVersionDTO fileVersionDTO, final ProgressListener progressListener) {
		fileDataAsyncDAO.getByFileVersionId(fileVersionDTO.getId(), new AsyncCallback<FileDataJS>() {
			@Override
			public void onFailure(Throwable caught) {
				progressListener.onFailure(Cause.CACHE_ERROR);
			}
			
			@Override
			public void onSuccess(FileDataJS fileDataJS) {
				if(fileDataJS == null) {
					queueTransfert(TransfertJS.createTransfertJS(fileVersionDTO, TransfertType.DOWNLOAD), progressListener);
					
				} else {
					startDownload(fileVersionDTO, fileDataJS.getData());
				}
			}
		});
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cache(final FileVersionDTO fileVersionDTO) {
		fileDataAsyncDAO.getByFileVersionId(fileVersionDTO.getId(), new AsyncCallback<FileDataJS>() {

			@Override
			public void onFailure(Throwable caught) {
				Log.error("Error while saving locally the file '" + fileVersionDTO.getName() + "'.", caught);
			}

			@Override
			public void onSuccess(FileDataJS result) {
				if(result == null) {
					queueTask(new Task(TransfertJS.createTransfertJS(fileVersionDTO, TransfertType.DOWNLOAD), null));
				}
			}
		});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void isCached(FileVersionDTO fileVersionDTO, final AsyncCallback<Boolean> callback) {
		fileDataAsyncDAO.getByFileVersionId(fileVersionDTO.getId(), new AsyncCallback<FileDataJS>() {
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
			
			@Override
			public void onSuccess(FileDataJS fileDataJS) {
				callback.onSuccess(fileDataJS != null);
			}
		});
	}
	
    /**
     * Convert <code>data</code> into a dataUrl and start the download from the
     * client browser.
     * 
     * @param fileVersion Information about the downloaded file.
     * @param data Array of bytes containing the data of the downloaded file.
     */
    private void startDownload(final FileVersionDTO fileVersion, final Int8Array data) {
		final JsArray<Int8Array> array = Values.createTypedJavaScriptArray(Int8Array.class);
		array.push(data);
		final Blob blob = Blob.createBlob(array, FileType.fromExtension(fileVersion.getExtension(), FileType._DEFAULT).getContentType());
					
		final FileReader fileReader = new FileReader();
		fileReader.addLoadFileListener(new LoadFileAdapter() {
			@Override
			public void onLoad() {
				startDownload(fileVersion, fileReader.getResultAsString());
			}
		});
		fileReader.readAsDataURL(blob);
	}
	
    /**
     * Ask the browser to download the given file.
     * 
     * @param fileVersion Information about the downloaded file.
     * @param dataUrl Content of the file as a data URL.
     */
	private void startDownload(FileVersionDTO fileVersion, String dataUrl) {
		Files.startDownload(fileVersion.getName() + '.' + fileVersion.getExtension(), dataUrl);
	}
	
	public void onDownloadComplete(FileVersionDTO fileVersionDTO, Int8Array data, boolean startDownload, TransfertThread transfertThread) {
		final FileVersionJS fileVersionJS = FileVersionJS.toJavaScript(fileVersionDTO);
		fileDataAsyncDAO.saveOrUpdate(FileDataJS.createFileDataJS(fileVersionJS, data));
		
		if(startDownload) {
			startDownload(fileVersionDTO, data);
		}
        
		nextDownload();
	}
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void canDownload(FileVersionDTO fileVersionDTO, final AsyncCallback<Boolean> callback) {
        if(state == ApplicationState.ONLINE) {
            callback.onSuccess(fileVersionDTO.isAvailable());
			
        } else {
            fileDataAsyncDAO.getByFileVersionId(fileVersionDTO.getId(), new AsyncCallback<FileDataJS>() {

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }

                @Override
                public void onSuccess(FileDataJS result) {
                    callback.onSuccess(result != null);
                }
            });
        }
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getDownloadQueueSize() {
		int size = downloads.size();
		if(currentTasks[TransfertType.DOWNLOAD.ordinal()] > 0) {
			size -= currentTasks[TransfertType.DOWNLOAD.ordinal()];
		}
		return size;
	}
	
    // Uploads -----------------------------------------------------------------
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void upload(FormPanel formPanel, final ProgressListener progressListener) {
		final HashMap<String, String> properties = new HashMap<String, String>();
		
		Blob blob = null;
		for(final Field<?> field : formPanel.getFields()) {
			if(field instanceof ButtonFileUploadField) {
				final ButtonFileUploadField fileField = (ButtonFileUploadField) field;
						
				if(blob != null) {
					throw new IllegalStateException("Multiple files have been found in the given form.");
				}
				blob = Blob.getBlob(fileField);
				
			} else if(field.getName() != null && field.getValue() instanceof String) {
				// BUGFIX #781: Ignoring fields with invalid values to avoid serialization errors when synchronizing.
				properties.put(field.getName(), (String) field.getValue());
			}
		}
		
		if(blob == null) {
			throw new IllegalStateException("No file have been found in the given form.");
		}
		
		prepareFileUpload(blob, properties, progressListener);
	}

	@Override
	public void uploadAvatar(FormPanel formPanel, ProgressListener progressListener) {
		// TODO: Make it offline
		// The previous system based on TransfertJS is too closely related to versioned files which is not used by contacts
		directTransfertManager.uploadAvatar(formPanel, progressListener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resumeUpload(final TransfertJS transfertJS) {
		queueTransfert(transfertJS, new ProgressAdapter() {
			// No action
		});
	}
	
    private void prepareFileUpload(final Blob blob, final Map<String, String> properties, final ProgressListener progressListener) {
		final String fileName = blob.getName();
		
		dispatchAsync.execute(new PrepareFileUpload(fileName, blob.getSize(), properties), new AsyncCallback<FileVersionDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.error("An error occured while preparing the upload of file '" + fileName + "'.", caught);
				progressListener.onFailure(Cause.SERVER_ERROR);
			}

			@Override
			public void onSuccess(final FileVersionDTO fileVersion) {
				final TransfertJS transfertJS = TransfertJS.createTransfertJS(fileVersion, TransfertType.UPLOAD);
				final FileReader fileReader = new FileReader();
				
				transfertJS.setProperties(properties);
				
				fileReader.addLoadFileListener(new LoadFileAdapter() {
					@Override
					public void onLoad() {
						final ArrayBuffer arrayBuffer = fileReader.getResultAsArrayBuffer();
						final Int8Array int8Array = Int8Array.createInt8Array(arrayBuffer);

						transfertJS.setData(int8Array);
						
						final FileDataJS fileDataJS = Values.createJavaScriptObject(FileDataJS.class);
						fileDataJS.setData(int8Array);
						fileDataJS.setMimeType(blob.getType());
						if (fileVersion != null) {
							fileDataJS.setFileVersion(FileVersionJS.toJavaScript(fileVersion));
						}

						fileDataAsyncDAO.saveOrUpdate(fileDataJS);
						
                        transfertAsyncDAO.saveOrUpdate(transfertJS, new AsyncCallback<TransfertJS>() {

                            @Override
                            public void onFailure(Throwable caught) {
                                N10N.offlineNotif(I18N.CONSTANTS.error(), I18N.CONSTANTS.offlineTransfertUploadStoreError(), eventBus);
                                queueTransfert(transfertJS, progressListener);
                            }

                            @Override
                            public void onSuccess(TransfertJS result) {
                                queueTransfert(transfertJS, progressListener);
                            }
                        });
					}
					@Override
					public void onError() {
						progressListener.onFailure(Cause.BLOB_READ_ERROR);
					}
				});

				fileReader.readAsArrayBuffer(blob);
			}
		});
	}
	
    public void onUploadComplete(TransfertThread transfertThread) {
		nextUpload();
	}
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canUpload() {
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getUploadQueueSize() {
		int size = uploads.size();
		if(currentTasks[TransfertType.UPLOAD.ordinal()] > 0) {
			size -= currentTasks[TransfertType.UPLOAD.ordinal()];
		}
		return size;
	}
    
    // Global Progress ---------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void setProgressListener(TransfertType type, ProgressListener progressListener) {
        progressListeners.put(type, progressListener);
    }

	/**
	 * {@inheritDoc}
	 */
    @Override
    public void removeProgressListener(TransfertType type) {
        progressListeners.remove(type);
    }
    
    public void onProgress() {
        fireProgress();
    }
    
    protected void fireProgress() {
		fireProgress(progressListeners.get(TransfertType.DOWNLOAD), downloadThread, downloads, TransfertType.DOWNLOAD);
		fireProgress(progressListeners.get(TransfertType.UPLOAD), uploadThread, uploads, TransfertType.UPLOAD);
    }
	
	private void fireProgress(ProgressListener progressListener, TransfertThread thread, List<Task> tasks, TransfertType type) {
		if(progressListener != null && currentTasks[type.ordinal()] >= 0) {
			double progress = currentTasks[type.ordinal()];
			double speed = 0.0;

			if(thread.getTask() != null) {
				progress += thread.getProgress();
				speed = thread.getSpeed();
			}
			
			progress /= tasks.size();
			
			progressListener.onProgress(progress, speed);
		}
	}

}
