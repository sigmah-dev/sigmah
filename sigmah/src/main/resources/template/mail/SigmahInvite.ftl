<#-- @ftlvariable name="" type="org.sigmah.server.mail.Invitation" -->
Hi ${newUser.name},

${invitingUser.name} (${invitingUser.email}) has invited you to access Sigmah. To
complete your user registration, click on the following link:

${userConfirmServerUrl}${newUser.changePasswordKey}

Best regards,

The team Sigmah