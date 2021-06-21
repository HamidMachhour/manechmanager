package com.manech.app.view.user;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;

import com.manech.app.LoginActivity;
import com.manech.app.R;
import com.manech.app.data.User;
import com.manech.app.helper.DownloadImageTask;
import com.manech.app.helper.StringValidation;
import com.manech.app.model.Injection;
import com.manech.app.model.UserViewModel;

import java.io.File;

import static android.app.Activity.RESULT_OK;


public class UserFragment extends Fragment {
    private UserViewModel userViewModel;
    private String imageUrl;
    private ImageView imageView;
    private EditText txtUserName;
    private  EditText txtPassword;
    private EditText txtCurrentPassword;
    private EditText txtConfirm;
    private CheckBox checkBox;
    private  TextView txtEmail;

    private final StringValidation stringValidation=new StringValidation();
    private long userId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view =inflater.inflate(R.layout.fragment_user, container, false);

        initForm(view);
        initModelObserver();

        return view;
    }

    private void initModelObserver() {

        final FragmentActivity activity = getActivity();

        userId = activity.getApplicationContext()
                .getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)
                .getLong(getString(R.string.saved_user_id), -1L);

        userViewModel= Injection.provideViewModelFactory(activity).create(UserViewModel.class);
        userViewModel.fetchUser(userId);
        userViewModel.getCurrentUser().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if(user!=null) {
                    fillForm(user);
                }else{
                    activity.finishAffinity();
                    startActivity(new Intent(activity, LoginActivity.class));
                }
            }
        });
    }

    private void initForm(View view) {
        txtEmail= view.findViewById(R.id.txtEmail);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtPassword=view.findViewById(R.id.txtPassword);
        txtCurrentPassword = view.findViewById(R.id.txtCurrentPassword);
        txtConfirm=  view.findViewById(R.id.txtConfirm);

        final FrameLayout frameLayout=view.findViewById(R.id.changePwdArea);
        checkBox=view.findViewById(R.id.chkChangePwd);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int id = buttonView.getId();
                if(id == R.id.chkChangePwd)
                {
                    if(checkBox.isChecked()){
                        frameLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        frameLayout.setVisibility(View.GONE);
                    }
                }
            }
        });

        imageView = view.findViewById(R.id.userImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
    }

    private void fillForm(User user) {
        txtCurrentPassword.setText("");
        txtPassword.setText("");
        txtConfirm.setText("");
        checkBox.setChecked(false);

        imageUrl=user.getUrlPicture();
        txtEmail.setText(user.getEmail());
        txtUserName.setText(user.getUserName());
        new DownloadImageTask(imageView).execute(imageUrl);
    }

    private  void save() {
        final FragmentActivity fragmentActivity = requireActivity();
        String userName=txtUserName.getText().toString();

        if(checkBox.isChecked()) {
            String password=  txtPassword.getText().toString();
            String oldPassword=  txtCurrentPassword.getText().toString();
            String confirm=  txtConfirm.getText().toString();

            if(!stringValidation.isValidPassword(password))
            {
                Toast.makeText(fragmentActivity,
                        getString(R.string.password_warning), Toast.LENGTH_LONG).show();
            }
            else if(!confirm.equals(password))
            {
                Toast.makeText(fragmentActivity,
                        getString(R.string.password_confirm_warning), Toast.LENGTH_LONG).show();
            }
            else{
                userViewModel.updateUser(userId,userName,imageUrl,oldPassword,password);
            }
        }else{
            userViewModel.updateUser(userId,userName,imageUrl,null,null);
        }
    }

    private void selectImage() {
        final CharSequence[] options = {
                getString(R.string.take_photo_option),
                getString(R.string.choose_from_gallery_option),
                getString(R.string.cancel_option)
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle(R.string.add_photo_title);
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (item==0)
                {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    final String profile_path=android.os.Environment.getExternalStorageDirectory() + File.separator
                            + getString(R.string.app_name)+ File.separator + "PROFILE";
                    File dir = new File (profile_path);
                    if (!dir.exists())
                    {
                        dir.mkdirs();
                    }
                    File f = new File(profile_path, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    final Uri uri = Uri.fromFile(f);
                    imageUrl=uri.getPath();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 1);
                }
                else if (item==1)
                {
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                }
                else if (item==2) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
             if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getActivity().getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                imageUrl = c.getString(columnIndex);
                c.close();
            }
            new DownloadImageTask(imageView).execute(imageUrl);
        }
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.user_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_home){
            FragmentManager manager = requireActivity().getSupportFragmentManager();
            manager.popBackStack();
        }
        if (id == R.id.action_save) {
            save();
            return true;
        }
        if (id == R.id.action_delete_account) {
            new AlertDialog.Builder(requireActivity())
                    .setTitle(getString(R.string.delete_account))
                    .setMessage(R.string.delete_account_confirm)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int whichButton) {
                            userViewModel.deleteUser(userId);
                        }})
                    .setNegativeButton(android.R.string.no, null).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}