package dk.trustworks.personalassistant.dropbox;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxTeamClientV2;
import com.dropbox.core.v2.files.DbxUserFilesRequests;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.sharing.CreateSharedLinkWithSettingsErrorException;
import com.dropbox.core.v2.sharing.DbxUserSharingRequests;
import com.dropbox.core.v2.sharing.ListSharedLinksErrorException;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;
import com.dropbox.core.v2.team.TeamMemberInfo;
import dk.trustworks.personalassistant.cache.CacheHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by hans on 26/04/16.
 */
public class DropboxAPI {

    private static final String ACCESS_TOKEN = "er5JfC5WCOAAAAAAAACBHbjYDH7GWXxx_YzmAmRMOpP8JKiNmoQDNxVhVlNsQkSn";
    private final DbxRequestConfig config;
    private final DbxTeamClientV2 client;

    public DropboxAPI() {
        config = new DbxRequestConfig("dropbox/java-tutorial", "en_US");
        client = new DbxTeamClientV2(config, ACCESS_TOKEN);
    }

    public void getFile() {
        try {
            for (TeamMemberInfo teamMemberInfo : client.team().membersList().getMembers()) {
                System.out.println("teamMemberInfo = " + teamMemberInfo);
            }

            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();

            ListFolderResult result = files.listFolder("");
            while (true) {
                for (Metadata metadata : result.getEntries()) {
                    System.out.println(metadata.getPathLower());
                }

                if (!result.getHasMore()) {
                    break;
                }

                result = files.listFolderContinue(result.getCursor());
            }
        } catch (DbxException e) {
            e.printStackTrace();
        }
    }

    public byte[] getRandomFile(String folder) {
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();

            ListFolderResult result = files.listFolder(folder);
            Metadata metadata = result.getEntries().get(new Random().nextInt(result.getEntries().size()));
            DbxDownloader<FileMetadata> thumbnail = files.download(metadata.getPathLower());
            System.out.println("thumbnail = " + thumbnail);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            thumbnail.download(outputStream);
            return outputStream.toByteArray();

        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("no file");
        return new byte[0];
    }

    public byte[] getSpecificFile(String filePath) {
        try {
            DbxUserFilesRequests files = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").files();
            DbxDownloader<FileMetadata> file = files.download(filePath);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            file.download(outputStream);
            return outputStream.toByteArray();

        } catch (DbxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("no file");
        return new byte[0];
    }

    public String getFileURL(String filePath) {
        String relativeFilePath = filePath.replace("/Users/hans/Dropbox (TrustWorks ApS)","");
        CacheHandler cache = CacheHandler.createCacheHandler();

        SharedLinkMetadata sharedLink = null;
        try {
            Map<String, String> urls = cache.getMapCache().get("sharing", () -> {
                Map<String, String> urlMap = new HashMap<>();
                DbxUserSharingRequests sharing = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").sharing();
                for (SharedLinkMetadata url : sharing.listSharedLinks().getLinks()) {
                    urlMap.put(url.getPathLower(), url.getUrl());
                }
                return urlMap;
            });
            if(urls.containsKey(relativeFilePath.toLowerCase())) return urls.get(relativeFilePath.toLowerCase());
            /*
            DbxUserSharingRequests sharing = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").sharing();
            for (SharedLinkMetadata linkMetadata : sharing.listSharedLinks().getLinks()) {
                if(linkMetadata.getPathLower().equals(relativeFilePath.toLowerCase())) return linkMetadata.getUrl();
            }
            */
            DbxUserSharingRequests sharing = client.asMember("dbmid:AADXwqazXGNcBlqO-nhTZEHxyJNYga2FtLM").sharing();
            sharedLink = sharing.createSharedLinkWithSettings(relativeFilePath);
            return sharedLink.getUrl();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (ListSharedLinksErrorException e) {
            e.printStackTrace();
        } catch (CreateSharedLinkWithSettingsErrorException e) {
            e.printStackTrace();
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return "";
    }
}
