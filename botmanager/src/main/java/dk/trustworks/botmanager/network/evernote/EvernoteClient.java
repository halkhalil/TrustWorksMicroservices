package dk.trustworks.botmanager.network.evernote;

import com.evernote.auth.EvernoteAuth;
import com.evernote.auth.EvernoteService;
import com.evernote.clients.ClientFactory;
import com.evernote.clients.NoteStoreClient;
import com.evernote.clients.UserStoreClient;
import com.evernote.edam.error.EDAMSystemException;
import com.evernote.edam.error.EDAMUserException;
import com.evernote.thrift.TException;

/**
 * Created by hans on 14/06/16.
 */
public class EvernoteClient {

    private UserStoreClient userStore;
    private NoteStoreClient noteStore;
    private String newNoteGuid;

    public EvernoteClient() throws TException, EDAMSystemException, EDAMUserException {
        EvernoteAuth evernoteAuth = new EvernoteAuth(EvernoteService.SANDBOX, "");
        ClientFactory factory = new ClientFactory(evernoteAuth);
        userStore = factory.createUserStoreClient();

        boolean versionOk = userStore.checkVersion("TrustWorks Mother",
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MAJOR,
                com.evernote.edam.userstore.Constants.EDAM_VERSION_MINOR);
        if (!versionOk) {
            System.err.println("Incompatible Evernote client protocol version");
            System.exit(1);
        }

        // Set up the NoteStore client
        noteStore = factory.createNoteStoreClient();
    }
}
