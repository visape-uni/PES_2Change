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
import pes.twochange.services.NotificationSender;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivitiy";
    private String userSenderUid;
    private String userReciverUid;
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

        mRlView = (RelativeLayout) findViewById(R.id.rl_view);

        //ActionBar boton atras
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //Coger chat pasado como extra en el intent
        Chat chat = (Chat) getIntent().getExtras().getSerializable("chat");
        /*
        //crear chat
        chat = new Chat(userSenderUid, userReciverUid);*/

        userSenderUid = chat.getMessageSender();
        userReciverUid = chat.getMessageReciver();
        Log.d(TAG, userReciverUid);
        Log.d(TAG, userSenderUid);

        //Suscribirse al topic para recibir notificaciones de chat
        FirebaseMessaging.getInstance()
                .subscribeToTopic(userReciverUid);


        //Firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Referencia al chat
        mFirebaseChatRefSender = mFirebaseDatabase.getReference().child("chats").child(userSenderUid).child(userReciverUid);

        displayChatMessage();

        mFirebaseChatRefReciver = mFirebaseDatabase.getReference().child("chats").child(userReciverUid).child(userSenderUid);

        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                String content = messageInput.getText().toString();
                content = content.trim();
                if (!content.isEmpty()) {
                    mFirebaseChatRefSender.push().setValue(new Message(content, userSenderUid, userReciverUid));
                    mFirebaseChatRefReciver.push().setValue(new Message(content, userSenderUid, userReciverUid));

                    NotificationSender n = new NotificationSender();
                    n.sendNotification(userSenderUid);

                    messageInput.setText("");
                }
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        photoItem = menu.findItem(R.id.action_photo);

        if (mayRequestStoragePermission()) {
            photoItem.setEnabled(true);
            photoItem.getIcon().setAlpha(255);
        } else {
            // disabled
            photoItem.setEnabled(false);
            photoItem.getIcon().setAlpha(130);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_photo:
                showOptions();
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
                    //hacer algo con la imagen(bitmap)
                    break;
                case SELECT_PICTURE:
                    Uri path = data.getData();
                    //hacer algo con la uri de la imagen
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == MY_PERMISSIONS) {
            if ((grantResults.length == 2) && (grantResults[0] == PackageManager.PERMISSION_GRANTED) && (grantResults[1] == PackageManager.PERMISSION_GRANTED)) {
                Toast.makeText(ChatActivity.this, "Accepted permissions", Toast.LENGTH_SHORT).show();
                photoItem.getIcon().setAlpha(255);
                photoItem.setEnabled(true);
            }
        } else {
            showExplanation();
        }
    }

    private void showExplanation() {
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
        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, mFirebaseChatRefSender) {
            @Override
            protected void populateView(View v, Message model, int position) {

                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                LinearLayout layoutMessageContent = (LinearLayout) v.findViewById(R.id.layout_message_content);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) layoutMessageContent.getLayoutParams();
                if (model.getMessageSender().equals(userSenderUid)) {
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_send_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_recive_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }

                messageContent.setText(model.getMessageContent());
                messageSender.setText(model.getMessageSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
            }
        };

        messagesList.setAdapter(adapter);
    }
}
