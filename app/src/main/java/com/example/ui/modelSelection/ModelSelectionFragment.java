package com.example.ui.modelSelection;

import android.app.AlertDialog;
import android.content.DialogInterface;
import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.example.MainViewModel;
import com.example.data.RobotModel;
import com.example.rcvc.R;
import com.example.ui.HostActivity;
import com.example.ui.HostedFragment;
import com.example.ui.testRobot.TestRobotFragment;
import com.example.ui.editControls.EditControlsFragment;

import net.simonvt.numberpicker.NumberPicker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@SuppressLint("LogNotTimber")
public class ModelSelectionFragment extends HostedFragment {

    private static final String TAG = "ModelSelectionFragment";
    private static final int TAKE_PICTURE_REQUEST_CODE = 1;
    private static final int SELECT_PICTURE_REQUEST_CODE = 2;
    private NumberPicker modelPicker;
    private MainViewModel viewModel;
    private TextView modelDescription;
    private ImageView modelPicture;
    private boolean modelsExist;
    private Button useModel, editModel;
    private Context context;
    private String currentPhotoPath;

    public ModelSelectionFragment() {
        super(R.layout.model_selection);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(MainViewModel.class);

        TransitionInflater inflater = TransitionInflater.from(requireContext());
        setExitTransition(inflater.inflateTransition(R.transition.fade));
        setEnterTransition(inflater.inflateTransition(R.transition.slide));

        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        context = getContext();

        useModel = view.findViewById(R.id.button_use_model);
        editModel = view.findViewById(R.id.button_edit_model);


        useModel.setOnClickListener(this::onClickUseModel);
        editModel.setOnClickListener(this::onClickEditModel);

        modelPicker = view.findViewById(R.id.model_picker);

        modelsExist = false;
        refreshNumberPicker();

        modelPicker.setOnValueChangedListener(this::onSelectedModelChanged);

        modelDescription = view.findViewById(R.id.model_description_text);
        modelPicture = view.findViewById(R.id.model_picture);
        modelPicture.setOnClickListener(v -> loadNewImage());

        setModelDescription();
        updateModelPicture();

        getActivity().setTitle(R.string.title_model_selection);
    }

    private void loadNewImage() {
        if(modelsExist) {
            String takePicture = getResources().getString(R.string.take_picture);
            String selectPicture = getResources().getString(R.string.select_picture);
            String deletePicture = getResources().getString(R.string.delete_picture);
            final CharSequence[] optionsMenu = {takePicture, selectPicture, deletePicture};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setItems(optionsMenu, (dialog, which) -> {
                if (optionsMenu[which].equals(takePicture)) {
                    takePicture();
                } else if (optionsMenu[which].equals(selectPicture)) {
                    selectPicture();
                } else if (optionsMenu[which].equals(deletePicture)) {
                    deletePicture();
                } else {
                    Log.e(TAG, "This can't happen - I hope lel");
                }
            });
            builder.show();
        }else{
            ((HostActivity) getActivity()).showToast(getResources().getString(R.string.no_model_exists));
        }
    }

    private void takePicture() {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    Log.e(TAG, ex.getMessage());
                    failedTakingPicture();
                }
                if (photoFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(context,
                            "com.example.rcvc.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    takePictureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(takePictureIntent, TAKE_PICTURE_REQUEST_CODE);
                } else {
                    Log.e(TAG, "photoFile == null");
                    failedTakingPicture();
                }
            } else {
                Log.e(TAG, "takePictureIntent.resolveActivity(context.getPackageManager()) == null");
                failedTakingPicture();
            }
        } else {
            Log.e(TAG, "No camera feature!");
            failedTakingPicture();
        }
    }

    private void selectPicture() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto, SELECT_PICTURE_REQUEST_CODE);
    }

    private void deletePicture() {
        RobotModel robotModel = viewModel.getRobotModel(modelPicker.getValue());
        File img = new File(robotModel.picture);
        if (!img.delete())
            try {
                if (img.getCanonicalFile().delete())
                    getContext().deleteFile(img.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }

        viewModel.setImageOfSelectedModel(null);
        updateModelPicture();
    }

    private void failedTakingPicture() {
        ((HostActivity) getActivity()).showToast(getResources().getString(R.string.error_taking_picture));
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE_REQUEST_CODE) {
                Uri savedImg = saveScaledDown(Uri.fromFile(new File(currentPhotoPath)));
                currentPhotoPath = savedImg.toString();
                viewModel.setImageOfSelectedModel(currentPhotoPath);
                updateModelPicture();
            } else if (requestCode == SELECT_PICTURE_REQUEST_CODE) {
                Uri selectedImageUri = null;
                if (data != null) {
                    selectedImageUri = data.getData();
                } else {
                    ((HostActivity) getActivity()).showToast(getResources().getString(R.string.error_selecting_picture));
                    Log.e(TAG, "data==null");
                }

                Uri savedImg = saveScaledDown(selectedImageUri);
                currentPhotoPath = savedImg.toString();
                viewModel.setImageOfSelectedModel(currentPhotoPath);
                updateModelPicture();
            }
        } else {
            Log.e(TAG, "resultCode != RESULT_OK");
        }
    }

    /**
     * save a scaled down copy of selected image in app specific storage
     * @param imageUri uri of selected image
     * @return uri of scaled down copy
     */
    private Uri saveScaledDown(Uri imageUri) {
        // get bitmap from uri
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // scale down to 480p (for now)
        Bitmap resizedBitmap;
        int maxSize = 480;
        if (bitmap.getWidth() > bitmap.getHeight()) {
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, maxSize, maxSize * bitmap.getHeight() / bitmap.getWidth(), false);
        } else {
            resizedBitmap = Bitmap.createScaledBitmap(bitmap, maxSize * bitmap.getWidth() / bitmap.getHeight(), maxSize, false);
        }

        // get app private directory
        File dir = context.getDir("imageDir", Context.MODE_PRIVATE);
        // create directory for new image file
        File imgPath = new File(dir, new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date()) + ".jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(imgPath);
            // save as jpg
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 95, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(imgPath);
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMANY).format(new Date());
        String imageFileName = "ModelPicture_" + timeStamp;
        File imageDir = getImageDir();
        File imageFile = File.createTempFile(imageFileName, ".jpg", imageDir);

        currentPhotoPath = imageFile.getAbsolutePath();
        return imageFile;
    }

    private File getImageDir() {
        //File imageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        Log.d(TAG, "1imagedir:" + imageDir.getAbsolutePath());
        if (!imageDir.exists()) {
            imageDir.mkdirs();
        }
        Log.d(TAG, "2imagedir:" + imageDir.getAbsolutePath());
        return imageDir;
    }

    private void updateModelPicture() {
        RobotModel robotModel = viewModel.getRobotModel(modelPicker.getValue());
        if (robotModel == null || robotModel.picture == null) {
            modelPicture.setImageResource(R.drawable.no_image_available);
            Log.d(TAG, "default pic");
        } else {
            modelPicture.setImageURI(Uri.parse(robotModel.picture));
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_model, menu);
    }

    private void refreshNumberPicker() {
        String[] allRobotNames = viewModel.getAllRobotNames();
        int maxVal = allRobotNames.length - 1;
        if (maxVal < 0) {
            maxVal = 0;
            modelsExist = false;
        } else {
            modelsExist = true;
        }
        modelPicker.setMaxValue(maxVal);
        modelPicker.setMinValue(0);
        if (modelsExist)
            modelPicker.setDisplayedValues(allRobotNames);
        else
            modelPicker.setDisplayedValues(new String[]{"Keine Modelle vorhanden."});
        modelPicker.setValue(viewModel.getSelectedModelPosition());

        if (modelsExist)
            editModel.setText(R.string.edit_model);
        else
            editModel.setText(R.string.create_model);

        useModel.setEnabled(modelsExist);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_add) {
            viewModel.modelSelected(-1);

            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, EditControlsFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
            return true;
        } else if (id == R.id.menu_delete) {
            if(modelsExist) {
                DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                                // delete model and remove from view
                                int v = modelPicker.getValue();
                                viewModel.deleteModelByPosition(v);

                                refreshNumberPicker();
                                setModelDescription();
                                updateModelPicture();
                        case DialogInterface.BUTTON_NEGATIVE:
                            dialog.dismiss();
                            break;
                    }

                };
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Dieses Modell löschen?:\n\"" + viewModel.getRobotModel(modelPicker.getValue()).name + "\"")
                        .setPositiveButton("Ja", dialogClickListener)
                        .setNegativeButton("Nein", dialogClickListener)
                        .show();
                return true;
            }else{
                ((HostActivity) getActivity()).showToast(getResources().getString(R.string.no_model_exists));
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Log.d(TAG, "Position: "+viewModel.getSelectedModelPosition());
        modelPicker.setValue(viewModel.getSelectedModelPosition());
    }

    private void onSelectedModelChanged(NumberPicker modelPicker, int oldVal, int newVal) {
        viewModel.setSelectedModelPosition(modelPicker.getValue());
        setModelDescription();
        updateModelPicture();
    }

    private void setModelDescription() {
        RobotModel robotModel;
        String descriptionText = "";
        if (modelsExist) {
            robotModel = viewModel.getRobotModel(modelPicker.getValue());
        } else {
            robotModel = new RobotModel(-1, "", "", "", "");
        }
        String typeString = (modelsExist) ? "(" + robotModel.type + "):" : "";
        descriptionText = robotModel.description;
        if (descriptionText == null || descriptionText.isEmpty())
            descriptionText = robotModel.specs;
        modelDescription.setText(robotModel.name + typeString + descriptionText);
    }

    private void onClickEditModel(View v) {
        if (modelsExist)
            viewModel.modelSelected(modelPicker.getValue());
        else
            viewModel.modelSelected(-1);

        FragmentManager fragmentManager = getParentFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container_view, EditControlsFragment.class, null, getResources().getString(R.string.fragment_tag_hosted))
                .setReorderingAllowed(true)
                .addToBackStack(null)
                .commit();
    }

    private void onClickUseModel(View v) {
        if (modelsExist) {
            viewModel.modelSelected(modelPicker.getValue());

            FragmentManager fragmentManager = getParentFragmentManager();
            Bundle bundle = new Bundle();
            bundle.putInt("cameFromModelSelection", 1);

            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container_view, TestRobotFragment.class, bundle, getResources().getString(R.string.fragment_tag_hosted))
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void robotConnectionStatusChanged(Integer newConnectionStatus) {
        //0 is not tested, 1 is connected, 2 is could not connect, 3 is connection lost, 4 connection is accepted = correct device, 5 connection is not accepted = wrong device
        switch (newConnectionStatus) {
            case 3:
                Log.d(TAG, "Case 3: Connection lost!");
                ((HostActivity) getActivity()).showToast(getResources().getString(R.string.connection_lost));
                break;

            default:
                Log.d(TAG, "Default: Something strange or nothing(Case -1) happend with the connection.");
                break;
        }

    }
}
