package pes.twochange.presentation.activity;

import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PersistableBundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Message;
import pes.twochange.domain.themes.ChatTheme;
import pes.twochange.domain.themes.SettingsTheme;
import pes.twochange.presentation.controller.BaseActivity;
import pes.twochange.services.NotificationSender;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends BaseActivity {

    private static final String TAG = "ChatActivitiy";
    private String userSender;
    private String userReciver;
    private DatabaseReference mFirebaseChatRefSender;
    private DatabaseReference mFirebaseChatRefReciver;
    FloatingActionButton sendBtn;

    private static String APP_DIRECTORY = "My2ChangeApp/";
    private static String MEDIA_DIRECTORY = APP_DIRECTORY + "2ChangeApp";

    private final int MY_PERMISSIONS = 100;
    private final int PHOTO_CODE = 200;
    private final int SELECT_PICTURE = 300;

    private RelativeLayout mRlView;
    private MenuItem photoItem;

    private String mPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Relative view
        mRlView = (RelativeLayout) findViewById(R.id.rl_view);

        //ActionBar boton atras
        /*ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/

        //Coger chat pasado como extra en el intent
        final Chat chat = (Chat) getIntent().getExtras().getSerializable("chat");

        //User sender
        userSender = chat.getMessageSender();
        //User reciver
        userReciver = chat.getMessageReciver();

        //Suscribirse al topic para recibir notificaciones de chat
        FirebaseMessaging.getInstance()
                .subscribeToTopic(userReciver);

        //Instance to Firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();

        //Firebase ref to sender's chat
        mFirebaseChatRefSender = mFirebaseDatabase.getReference().child("chats").child(userSender).child(userReciver);

        //Firebase ref to reciver's chat
        mFirebaseChatRefReciver = mFirebaseDatabase.getReference().child("chats").child(userReciver).child(userSender);

        //Display the messages of the DB into the list view
        displayChatMessage();

        //OnClickListener to send messages
        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                //Get the message to string
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                String content = messageInput.getText().toString();

                //Delete blank spaces of the messages
                content = content.trim();

                if (!content.isEmpty()) {

                    ChatTheme.getInstance(chat).sendChatMessage(content);

                    //Put the text field empty again
                    messageInput.setText("");
                }
            }
        });
    }

    @Override
    protected int currentMenuItemIndex() {
        return CHAT_ACTIVITY;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        photoItem = menu.findItem(R.id.action_photo);

        //IF PHOTO PERMISIONS ARE ACCEPTED SHOW THE ICON TO SEND PICTURES
        if (mayRequestStoragePermission()) {
            photoItem.setEnabled(true);
            photoItem.getIcon().setAlpha(255);
        } else {
            // PICTURE'S ICON DISABLED
            photoItem.setEnabled(false);
            photoItem.getIcon().setAlpha(130);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                //IF Picture's icon is selected show gallery or camera
                showOptions();
                break;
            case R.id.send_details:
                Chat chat = new Chat(userSender,userReciver);
                ChatTheme.getInstance(chat).sendContactDetails();
                break;
            case R.id.block_user:
                SettingsTheme.getInstance(userSender).blockUser(userReciver);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private boolean mayRequestStoragePermission () {
        //mirar si android version es menor a la 6 no hace falta permisos
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) return true;
        //mirar si permisos ya estan aceptados
        if ((checkSelfPermission(WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) &&
                (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_GRANTED)) return true;

        //ASK PERMISIONS TO ACCESS TO THE CAMERA AND GALLERY (FOR PICTURES)
        if ((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE)) || (shouldShowRequestPermissionRationale(CAMERA))) {
            Snackbar.make(mRlView, "Permisions are necessary", Snackbar.LENGTH_INDEFINITE).setAction(android.R.string.ok, new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
                }
            }).show();
        } else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, CAMERA}, MY_PERMISSIONS);
        }

        return false;
    }

    private void showOptions() {
        //Show options to take a picture, open the gallery or cancel

        final CharSequence[] option = {"Take a picture", "Gallery", "Cancel"};
        final AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Choose an option:");
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        openCamera();
                        break;
                    case 1:
                        //Open the gallery
                        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(intent.createChooser(intent, "Select photo app"), SELECT_PICTURE);
                        break;
                    case 2:
                        dialog.dismiss();
                        break;
                }
            }
        });
        builder.show();
    }

    private void openCamera() {
        //OPEN THE CAMERA
        File file = new File(Environment.getExternalStorageDirectory(), MEDIA_DIRECTORY);
        boolean isDirectoryCreated = file.exists();
        if (!isDirectoryCreated) isDirectoryCreated = file.mkdirs();
        if (isDirectoryCreated) {
            Long timestamp = System.currentTimeMillis()/1000;
            String imageName = timestamp.toString() + ".jpg";

            mPath = Environment.getExternalStorageDirectory() + File.separator + MEDIA_DIRECTORY + File.separator + imageName;

            File newFile = new File(mPath);

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(newFile));
            startActivityForResult(intent, PHOTO_CODE);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //Save path when it goes to the camera
        outState.putString("file_path", mPath);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mPath = savedInstanceState.getString("file_path");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_OK) {
            switch (requestCode) {
                case PHOTO_CODE:
                    MediaScannerConnection.scanFile(this, new String[]{mPath}, null, new MediaScannerConnection.OnScanCompletedListener() {
                        @Override
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i(TAG, "Scanned " + path + ":");
                            Log.i(TAG, "-> Uri = " + uri);
                        }
                    });
                    Bitmap bitmap = BitmapFactory.decodeFile(mPath);
                    //INCOMPLETE
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    //INCOMPLETE
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS) {
            if ((grantResults.length == 2) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {

                //If permissions accepted enable the picture's icon
                Toast.makeText(ChatActivity.this, "Accepted permissions", Toast.LENGTH_SHORT).show();
                photoItem.getIcon().setAlpha(255);
                photoItem.setEnabled(true);
            }
        } else {

            //If permissions denied request for permisions
            showExplanation();
        }
    }

    private void showExplanation() {

        //Explanation why are the permissions needed
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Permissions denied");
        builder.setMessage("Permits are necessary to use app functionalities");
        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    private void displayChatMessage() {

        ListView messagesList = (ListView)findViewById(R.id.messages_list);

        //Firebase adapter for getting the messages
        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, mFirebaseChatRefSender) {
            @Override
            protected void populateView(View v, Message model, int position) {

                //Getting the textviews of the Message's layout
                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                LinearLayout layoutMessageContent = (LinearLayout) v.findViewById(R.id.layout_message_content);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) layoutMessageContent.getLayoutParams();
                if (model.getMessageSender().equals(userSender)) {
                    //If it's a message from the sender use the green (message) and align right
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_send_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    //If it's a message from the reciver use the orange (message) and align left
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_recive_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }

                //Set with the content of the message, the message sender, and the time when the message was sent
                messageContent.setText(model.getMessageContent());
                messageSender.setText(model.getMessageSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
            }
        };

        //Use the firebase adapter on the listview
        messagesList.setAdapter(adapter);
    }
}
