package com.devtech.gestiondestock.services.impl;

import com.devtech.gestiondestock.GestionDeStockApplication;
import com.devtech.gestiondestock.services.GoogleDriveService;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
import com.google.api.services.drive.model.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

@Service
@Slf4j
public class GoogleDriveServiceImpl implements GoogleDriveService {

    private static final String APPLICATION_NAME = "Gestion de stock v1";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "src/main/resources/tokens/";
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    @Override
    public String savePhoto(InputStream photo, String context, Integer id) throws Exception {
        Drive service = buildDriverService();
        java.io.File filePath = null;
        if (photo == null) {
            filePath = new java.io.File("/home/luca/Images/1/image.png");
        }else {
            filePath = convertInputStreamToFile(photo, context + "_" + id);
        }
        File folderContext = isFolderContextExist();
        if ( folderContext == null){
            folderContext = buildFolderContext();
        }

        File fileMetadata = new File();
        fileMetadata.setName(context + "_" + id + ".jpeg");
        fileMetadata.setParents(Arrays.asList(folderContext.getId()));
        FileContent mediaContent = new FileContent("image/jpeg", filePath);
        try {
            File file = service.files().create(fileMetadata, mediaContent)
                    .setFields("id, parents")
                    .execute();
            sharedFile(file.getId());
            filePath.deleteOnExit();
            return "https://drive.google.com/file/d/" + file.getId() + "/view";
        } catch (GoogleJsonResponseException e) {
            System.err.println("Unable to upload file: " + e.getDetails());
            throw e;
        }
    }

    private Drive buildDriverService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        return new Drive.Builder(HTTP_TRANSPORT, JSON_FACTORY, getCredentials(HTTP_TRANSPORT))
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    private File buildFolderContext() throws Exception {
        Drive service = buildDriverService();
        File fileMetadata = new File();
        fileMetadata.setName("Gestion de stock");
        fileMetadata.setMimeType("application/vnd.google-apps.folder");
        try {
            File file = service.files().create(fileMetadata)
                    .setFields("id")
                    .execute();
            creatPermission(file);
            return file;
        } catch (GoogleJsonResponseException e) {
            throw e;
        }
    }

    private File isFolderContextExist() throws Exception {
        Drive service = buildDriverService();
        List<File> files = new ArrayList<>();
        String pageToken = null;
        do {
            FileList result = service.files().list()
                    .setPageSize(10)
                    .setFields("nextPageToken, files(id, name)")
                    .setPageToken(pageToken)
                    .execute();
            for (File file : result.getFiles()) {
                if ("Gestion de stock".equals(file.getName())) {
                    creatPermission(file);
                    return file;
                }
            }
            files.addAll(result.getFiles());
            pageToken = result.getNextPageToken();
        } while (pageToken != null);
        return null;
    }

    private void creatPermission(File file) throws Exception {
        Drive service = buildDriverService();
        Permission permission = new Permission();
        permission.setType("user");
        permission.setRole("writer");
        permission.setEmailAddress("miarinantenainaluca@gmail.com");
        service.permissions().create(file.getId(), permission).execute();
    }

    private Permission sharedFile(String fileId) throws Exception{
        Drive service = buildDriverService();
        Permission permission = new Permission();
        permission.setType("anyone");
        permission.setRole("reader");
        permission.setAllowFileDiscovery(true);
        return service.permissions().create(fileId, permission).execute();
    }

    /**
     * Creates an authorized Credential object.
     *
     * @param HTTP_TRANSPORT The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    private static Credential getCredentials(final NetHttpTransport HTTP_TRANSPORT) throws Exception {
        InputStream in = GestionDeStockApplication.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private java.io.File convertInputStreamToFile(InputStream inputStream, String fileName) throws IOException {
        java.io.File file = new java.io.File(fileName);
        OutputStream outputStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        outputStream.close();
        inputStream.close();
        return file;
    }

}
