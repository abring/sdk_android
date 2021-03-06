package ir.abring.abringservices.ui.fragment.mobileRegister;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mvc.imagepicker.ImagePicker;

import java.io.File;

import butterknife.BindView;
import ir.abring.abringlibrary.abringclass.user.AbringMobileRegister;
import ir.abring.abringlibrary.interfaces.AbringCallBack;
import ir.abring.abringlibrary.network.AbringApiError;
import ir.abring.abringlibrary.utils.AbringCheck;
import ir.abring.abringservices.R;
import ir.abring.abringservices.base.BaseFragment;

public class MobileRegisterFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.btnSave)
    Button btnSave;
    @BindView(R.id.etMobile)
    EditText etMobile;
    @BindView(R.id.inputlayoutMobile)
    TextInputLayout inputlayoutMobile;
    @BindView(R.id.etUsername)
    EditText etUsername;
    @BindView(R.id.inputlayoutUsername)
    TextInputLayout inputlayoutUsername;
    @BindView(R.id.etPassword)
    EditText etPassword;
    @BindView(R.id.inputlayoutPassword)
    TextInputLayout inputlayoutPassword;
    @BindView(R.id.etName)
    EditText etName;
    @BindView(R.id.inputlayoutName)
    TextInputLayout inputlayoutName;
    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;
    @BindView(R.id.linRegisterHolder)
    LinearLayout linRegisterHolder;
    @BindView(R.id.etCode)
    EditText etCode;
    @BindView(R.id.inputlayoutCode)
    TextInputLayout inputlayoutCode;
    @BindView(R.id.linActiveHolder)
    LinearLayout linActiveHolder;
    @BindView(R.id.tvResendActiveCode)
    TextView tvResendActiveCode;

    private File file = null;
    private boolean isActive = false;

    public MobileRegisterFragment() {
        // Required empty public constructor
    }

    @Override
    protected void initBeforeView() {

    }

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_mobile_register;
    }

    @Override
    protected void initViews(View rootView) {
        btnSave.setOnClickListener(this);
        imgAvatar.setOnClickListener(this);
        tvResendActiveCode.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                if (!isActive)
                    mobileRegister();
                else
                    mobileVerify();
                break;

            case R.id.imgAvatar:
                file = null;
                ImagePicker.pickImage(this, "Select your image:", 100, false);
                break;

            case R.id.tvResendActiveCode:
                mobileResendCode();
                break;
        }
    }

    private void mobileRegister() {

        final AbringMobileRegister abringUser = new AbringMobileRegister
                .MobileRegisterBuilder()
                .setMobile(etMobile.getText().toString())
                .build();

        abringUser.mobileRegister(mActivity, new AbringCallBack() {
            @Override
            public void onSuccessful(Object response) {
                Toast.makeText(getActivity(), "کد فعالسازی ارسال شد...", Toast.LENGTH_LONG).show();
                isActive = true;
            }

            @Override
            public void onFailure(Object response) {
                AbringApiError apiError = (AbringApiError) response;
                Toast.makeText(mActivity,
                        AbringCheck.isEmpty(apiError.getMessage()) ? getString(R.string.abring_failure_responce) : apiError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mobileVerify() {
        AbringMobileRegister.mobileVerify(etCode.getText().toString(), new AbringCallBack<Object, Object>() {
            @Override
            public void onSuccessful(Object response) {
                Toast.makeText(getActivity(), "ثبت نام با موفقیت انجام شد", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Object response) {
                AbringApiError apiError = (AbringApiError) response;
                Toast.makeText(mActivity,
                        AbringCheck.isEmpty(apiError.getMessage()) ? getString(R.string.abring_failure_responce) : apiError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mobileResendCode() {
        AbringMobileRegister.mobileResendCode(new AbringCallBack<Object, Object>() {
            @Override
            public void onSuccessful(Object response) {
                Toast.makeText(getActivity(), "منتظر دریافت کد جدید باشید", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Object response) {
                AbringApiError apiError = (AbringApiError) response;
                Toast.makeText(mActivity,
                        AbringCheck.isEmpty(apiError.getMessage()) ? getString(R.string.abring_failure_responce) : apiError.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        try {
            // When an Image is picked
            if (requestCode == 100 && resultCode == Activity.RESULT_OK && null != data) {

                Bitmap bitmap = ImagePicker.getImageFromResult(mActivity, requestCode, resultCode, data);
                if (bitmap != null) {
                    imgAvatar.setImageBitmap(bitmap);
                }

                // Get the Image from data
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = mActivity.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String mediaPath = cursor.getString(columnIndex);
                // Map is used to multipart the file using okhttp3.RequestBody
                file = new File(mediaPath);
                /*// Set the Image in ImageView for Previewing the Media
                imgView.setImageBitmap(BitmapFactory.decodeFile(mediaPath));*/
                cursor.close();

            } else {
                Toast.makeText(mActivity, "You haven't picked Image/Video", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(mActivity, "Something went wrong", Toast.LENGTH_LONG).show();
        }
    }
}
