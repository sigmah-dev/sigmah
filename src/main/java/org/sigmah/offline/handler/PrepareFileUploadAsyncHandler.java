package org.sigmah.offline.handler;

import java.util.ArrayList;
import java.util.Date;

import org.sigmah.offline.dao.ValueAsyncDAO;
import org.sigmah.offline.dispatch.AsyncCommandHandler;
import org.sigmah.offline.dispatch.OfflineExecutionContext;
import org.sigmah.offline.js.FileJS;
import org.sigmah.offline.js.FileVersionJS;
import org.sigmah.offline.js.ValueJS;
import org.sigmah.offline.js.ValueJSIdentifierFactory;
import org.sigmah.shared.command.GetValue;
import org.sigmah.shared.command.PrepareFileUpload;
import org.sigmah.shared.command.result.ValueResult;
import org.sigmah.shared.dto.element.FilesListElementDTO;
import org.sigmah.shared.dto.value.FileDTO;
import org.sigmah.shared.dto.value.FileUploadUtils;
import org.sigmah.shared.dto.value.FileVersionDTO;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.sigmah.offline.js.Values;

/**
 * JavaScript implementation of {@link org.sigmah.server.handler.PrepareFileUploadHandler}.
 * Used when the user is offline.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Singleton
public class PrepareFileUploadAsyncHandler implements AsyncCommandHandler<PrepareFileUpload, FileVersionDTO> {
	
	@Inject
	private ValueAsyncDAO valueAsyncDAO;

	@Override
	public void execute(final PrepareFileUpload command, final OfflineExecutionContext executionContext, final AsyncCallback<FileVersionDTO> callback) {
		// TODO: Stocker la demande d'upload et prévoir un mécanisme pour faire l'upload à la reconnexion
		// Garder une instance de TransfertJS avec "progress = 0" ?
		// Ou alors, simplement attendre la reconnexion pour obtenir l'instance de FileVersionDTO et là démarrer un TransfertJS.
		
		final String fileId = command.getProperties().get(FileUploadUtils.DOCUMENT_ID);
		final String projectId = command.getProperties().get(FileUploadUtils.DOCUMENT_PROJECT);
		final String elementId = command.getProperties().get(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT);
		final String fileName = command.getProperties().get(FileUploadUtils.DOCUMENT_NAME);
		
		final GetValue getValue = new GetValue();
		getValue.setProjectId(Integer.parseInt(projectId));
		getValue.setElementId(Integer.parseInt(elementId));
		getValue.setElementEntityName(new FilesListElementDTO().getEntityName());
		
		final String id = ValueJSIdentifierFactory.toIdentifier(getValue);
		valueAsyncDAO.get(id, new AsyncCallback<ValueResult>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.error("An error occured while retrieving the value of the field '" + id + "'.", caught);
			}
			@Override
			public void onSuccess(ValueResult valueResult) {
				final ValueJS valueJS = ValueJS.toJavaScript(getValue, valueResult);
				if(valueJS.getValues() == null) {
					valueJS.setValues(Values.createJavaScriptArray(JsArray.class));
				}
				
				final JsArray<FileJS> files = valueJS.getValues();
				
				FileJS updatedFile = null;
				if(fileId != null) {
					// Searching for the FileJS to update
					final int updatedFileId = Integer.parseInt(fileId);
					for(int index = 0; updatedFile == null && index < files.length(); index++) {
						final FileJS file = files.get(index);
						if(file.getId() == updatedFileId) {
							updatedFile = file;
						}
					}
				}
				
				if(updatedFile == null) {
					// Creating a new file
					final FileDTO fileDTO = new FileDTO();
					fileDTO.setId(-1);
					fileDTO.setName(command.getFileName());
					fileDTO.setVersions(new ArrayList<FileVersionDTO>());
					
					updatedFile = FileJS.toJavaScript(fileDTO);
					files.push(updatedFile);
				}
				
				// Creating a new version
				final FileVersionDTO fileVersionDTO = new FileVersionDTO();
				fileVersionDTO.setId(-1);
				fileVersionDTO.setAddedDate(new Date());
				fileVersionDTO.setAuthorFirstName(executionContext.getAuthentication().getUserFirstName());
				fileVersionDTO.setAuthorName(executionContext.getAuthentication().getUserName());
				fileVersionDTO.setSize(command.getSize());
				
				final String fullName = normalizeFileName(fileName);
				final int dotIndex = fullName.indexOf('.');

				final String name = dotIndex > 0 ? fullName.substring(0, dotIndex) : fullName;
				final String extension = dotIndex > 0 && dotIndex < fullName.length() ? fullName.substring(dotIndex + 1) : null;
				fileVersionDTO.setName(name);
				fileVersionDTO.setExtension(extension);
				
				int versionNumber = 1;
				final JsArray<FileVersionJS> versions = updatedFile.getVersions();
				for(int index = 0; index < versions.length(); index++) {
					final FileVersionJS version = versions.get(index);
					if(version.getVersionNumber() >= versionNumber) {
						versionNumber = version.getVersionNumber() + 1;
					}
				}
				fileVersionDTO.setVersionNumber(versionNumber);
				
				// Saving locally the new version
				final FileVersionJS createdVersion = FileVersionJS.toJavaScript(fileVersionDTO);
				versions.push(createdVersion);
				
				// Updating the value of the current flexible element before returning the new FileVersionJS
				valueAsyncDAO.saveOrUpdate(getValue, valueJS.toValueResult());
				
				callback.onSuccess(fileVersionDTO);
			}
		});
	}
	
	/**
	 * Removes the folder "C:\fakepath\" (used by Webkit browsers to hide the real path of the file).
	 * Also replaces characters that can't be used in Windows filenames by an underscore.
	 * 
	 * @param fileName
	 *            name to validate
	 * @return string the name validated
	 */
	private String normalizeFileName(String fileName) {
		if(fileName != null) {
			return fileName.replaceFirst("[cC]:\\\\fakepath\\\\", "").replaceAll("[\\/:*?\"<>|]", "_");
		} else {
			return "";
		}
	}
	
}
