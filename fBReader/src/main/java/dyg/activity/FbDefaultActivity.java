package dyg.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.binioter.guideview.Component;
import com.dyg.android.reader.R;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;
import com.yanzhenjie.permission.runtime.PermissionDef;

import org.geometerplus.android.fbreader.config.ConfigShadow;
import org.geometerplus.android.fbreader.library.DefaultBooks;
import org.geometerplus.android.fbreader.library.LibraryActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

// todo 第一步创建recyclerview
// 2.设置headerView
// 3.添加引导
// 创建试验

public class FbDefaultActivity extends FragmentActivity {
    public static final String PATH_BOOKS = "localbooks";
    public static final String PATH_ICONS = "localicons";
    private static final int RC_CAMERA_AND_LOCATION = 110;
    private static final String not_first_in_FbDefault = "not_first_in_FbDefault";

    public static void startActivity(Activity libraryActivity) {
        libraryActivity.startActivity(new Intent(libraryActivity, FbDefaultActivity.class));
    }

    public void showGuideView(final View view) {
        if (ConfigShadow.getInstance().getSpecialBooleanValue(not_first_in_FbDefault, false)) {
            return;
        }
        LibraryActivity.showGuideSimpleComponentView(view, "点击阅读该书", new Component.CallBack() {
            @Override
            public void callBackShown(View view) {

            }

            @Override
            public void callBackDismiss(View view) {
                ConfigShadow.getInstance().setSpecialBooleanValue(not_first_in_FbDefault, true);
            }

        }, FbDefaultActivity.this);

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_localbooks);
        BackendSupport.getInstance();
        requestPermission(this, Permission.READ_PHONE_STATE,
                Permission.WRITE_EXTERNAL_STORAGE, Permission.READ_EXTERNAL_STORAGE
        );
    }

    private static final String TAG = "permission";

    private void extractBooks() {
        BackendSupport.getInstance().postDelayed(0, new Runnable() {
            @Override
            public void run() {
                File file = new File(getFilesDir(), PATH_BOOKS);
                if (!file.exists()) {
                    extractFiles();
                }
                initDefaultBooks();
            }
        });
    }

    private void extractFiles() {
        File file = getFilesDir();
        // create localbools dir
        File localbooks = new File(file, PATH_BOOKS);
        File localicons = new File(file, PATH_ICONS);
        if (!localbooks.exists()) {
            localbooks.mkdir();
        }
        if (!localicons.exists()) {
            localicons.mkdir();
        }
        AssetManager manager = getAssets();
        try {
            String[] books = manager.list(PATH_BOOKS);
            copyFile2Dirs(books, manager, localbooks, PATH_BOOKS);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String[] icons = manager.list(PATH_ICONS);
            copyFile2Dirs(icons, manager, localicons, PATH_ICONS);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void copyFile2Dirs(String[] books, AssetManager manager, File destFile, String path) throws IOException {
        for (int i = 0; i < books.length; i++) {
            InputStream inputStream = manager.open(path + "/" + books[i]);
            writeFile(inputStream, destFile, books[i]);
        }
    }

    public void writeFile(InputStream inputStream, File desFile, String fileName) throws IOException {
        File file = new File(desFile, fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buff = new byte[1024];
        FileOutputStream fileOutputStream = null;
        try {
            int len;
            fileOutputStream = new FileOutputStream(file);
            while ((len = inputStream.read(buff, 0, buff.length)) != -1) {
                fileOutputStream.write(buff, 0, len);
            }
            fileOutputStream.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
            fileOutputStream.close();
        }
    }

    private void initDefaultBooks() {
        new DefaultBooks(FbDefaultActivity.this);
        Log.e(TAG, "initDefaultBooks: ");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // test code del

//        File file = new File(getFilesDir(), PATH_BOOKS);
//        for (File file1 : file.listFiles()) {
//            file1.delete();
//        }
//        file.delete();
//        File file2 = new File(getFilesDir(), PATH_ICONS);
//        for (File file3 : file2.listFiles()) {
//            file3.delete();
//        }
//        file2.delete();
    }

    /**
     * Request permissions.
     */
    public void requestPermission(final Context context,
                                  @PermissionDef String... permissions) {
        AndPermission.with(context)
                .runtime()
                .permission(permissions)
                .rationale(new RuntimeRationale())
                .onGranted(new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> permissions) {
                        Log.e(TAG, "onAction: ");
                        extractBooks();
                    }
                })
                .onDenied(new Action<List<String>>() {
                    @Override
                    public void onAction(@NonNull List<String> permissions) {
                        if (AndPermission.hasAlwaysDeniedPermission(context, permissions)) {
                            showSettingDialog(context, permissions);
                        }
                    }
                })
                .start();
    }

    /**
     * Display setting dialog.
     */
    public static void showSettingDialog(final Context context, final List<String> permissions) {
        List<String> permissionNames = Permission.transformText(context, permissions);
        String message = context.getString(R.string.message_permission_always_failed,
                TextUtils.join("\n", permissionNames));

        new AlertDialog.Builder(context).setCancelable(false)
                .setTitle(R.string.title_dialog)
                .setMessage(message)
                .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setPermission(context);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .show();
    }

    private static final int REQUEST_CODE_SETTING = 1;

    /**
     * Set permissions.
     */
    private static void setPermission(Context context) {
        AndPermission.with(context).runtime().setting().start(REQUEST_CODE_SETTING);
    }

}
