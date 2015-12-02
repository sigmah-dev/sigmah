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
import java.util.Map;
import org.sigmah.offline.dao.UpdateDiaryAsyncDAO;
import org.sigmah.offline.js.ListableValueJS;
import org.sigmah.offline.js.Values;
import org.sigmah.shared.command.result.Authentication;
import org.sigmah.shared.util.ValueResultUtils;

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
	
	@Inject
	private UpdateDiaryAsyncDAO updateDiaryAsyncDAO;

	@Override
	public void execute(final PrepareFileUpload command, final OfflineExecutionContext executionContext, final AsyncCallback<FileVersionDTO> callback) {
		// Saving the prepare upload request.
		updateDiaryAsyncDAO.saveWithNegativeId(command, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Integer result) {
				findAndUpdateFileVersion(command, result, executionContext.getAuthentication(), callback);
			}
		});
	}
	
	private void findAndUpdateFileVersion(final PrepareFileUpload command, final int versionId, 
			final Authentication authentication, final AsyncCallback<FileVersionDTO> callback) {
		final Map<String, String> properties = command.getProperties();
		
		final String fileId = properties.get(FileUploadUtils.DOCUMENT_ID);
		final String projectId = properties.get(FileUploadUtils.DOCUMENT_PROJECT);
		final String elementId = properties.get(FileUploadUtils.DOCUMENT_FLEXIBLE_ELEMENT);
		
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
					valueJS.setValues(Values.createTypedJavaScriptArray(ListableValueJS.class));
				}
				
				final JsArray<FileJS> files = valueJS.getValues();
				
				final FileVersionDTO fileVersionDTO = createFileVersion(fileId, 
					versionId, command, authentication, files);
				
				// Updating the value of the current flexible element before returning the new FileVersionJS
				valueAsyncDAO.saveOrUpdate(getValue, valueJS.toValueResult());
				
				callback.onSuccess(fileVersionDTO);
			}
		});
	}
	
	private FileVersionDTO createFileVersion(String fileId, int versionId, PrepareFileUpload command,
		Authentication authentication, JsArray<FileJS> files) throws NumberFormatException {
		
		FileJS updatedFile = null;
		if(fileId != null) {
			// Searching for the FileJS to update.
			final int updatedFileId = Integer.parseInt(fileId);
			for(int index = 0; updatedFile == null && index < files.length(); index++) {
				final FileJS file = files.get(index);
				if(file.getId() == updatedFileId) {
					updatedFile = file;
				}
			}
		}
		
		if(updatedFile == null) {
			// Creating a new file.
			final FileDTO fileDTO = new FileDTO();
			fileDTO.setId(versionId);
			fileDTO.setName(command.getFileName());
			fileDTO.setVersions(new ArrayList<FileVersionDTO>());

			updatedFile = FileJS.toJavaScript(fileDTO);
			files.push(updatedFile);
		}
		
		// Name and extension.
		final String path = command.getProperties().get(FileUploadUtils.DOCUMENT_NAME);
		final String fullName = ValueResultUtils.normalizeFileName(path);
		final int dotIndex = fullName.indexOf('.');
		final String name = dotIndex > 0 ? fullName.substring(0, dotIndex) : fullName;
		final String extension = dotIndex > 0 && dotIndex < fullName.length() ? fullName.substring(dotIndex + 1) : null;
		
		// Version number.
		int versionNumber = 1;
		final JsArray<FileVersionJS> versions = updatedFile.getVersions();
		for(int index = 0; index < versions.length(); index++) {
			final FileVersionJS version = versions.get(index);
			if(version.getVersionNumber() >= versionNumber) {
				versionNumber = version.getVersionNumber() + 1;
			}
		}
		
		// Creating the new version.
		final FileVersionDTO fileVersionDTO = new FileVersionDTO();
		fileVersionDTO.setId(versionId);
		fileVersionDTO.setAddedDate(new Date());
		fileVersionDTO.setAuthorFirstName(authentication.getUserFirstName());
		fileVersionDTO.setAuthorName(authentication.getUserName());
		fileVersionDTO.setSize(command.getSize());
		fileVersionDTO.setName(name);
		fileVersionDTO.setExtension(extension);
		fileVersionDTO.setVersionNumber(versionNumber);
		
		// Saving the new version locally.
		final FileVersionJS createdVersion = FileVersionJS.toJavaScript(fileVersionDTO);
		versions.push(createdVersion);
		
		return fileVersionDTO;
	}
	
}
