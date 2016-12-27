import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.activity.FriendsActivity;
import com.azinecllc.champy.helper.CHImageModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

import static com.azinecllc.champy.utils.Constants.path;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;


@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class Image_module {
    private CHImageModule imagemodule;
    private Context context;
    private FriendsActivity friendsActivity;

    @Before
    public void set_up() throws Exception {
        context = RuntimeEnvironment.application;
    }

//    @Test
//    public void test_Image_module_init() throws URISyntaxException, FileNotFoundException {
//        imagemodule = new CHImageModule(context);
//        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res";
//        Drawable dr = imagemodule.Init(filePath, friendsActivity);
//        assertNotNull("Ok!!", dr);
//    }


    //230, 52, 108, 117

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void test_For_Saving_To_Internal_Storage() {
        Bitmap bitmapImage = null;
        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res/blurred.png";

        try {
            InputStream in = new URL(path).openStream();
            bitmapImage = BitmapFactory.decodeStream(in);
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ContextWrapper cw = new ContextWrapper(context);
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/android/data/com.azinecllc.champy/images");
        myDir.mkdirs();

        Random generator = new Random();
        int n = 100000000;
        n = generator.nextInt(n);
        String fileName = "Image-" + n + ".jpg";

        File file = new File(myDir, fileName);
        if (file.exists()) {
            file.delete();
        }
        Bitmap bitmapImageeee = bitmapImage;
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmapImageeee != null) {
                bitmapImageeee.compress(Bitmap.CompressFormat.JPEG, 90, out);
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File myPath = new File(directory, "profile.jpg"); // Create imageDir
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(myPath);
            if (bitmapImageeee != null) {
                bitmapImageeee.compress(Bitmap.CompressFormat.PNG, 100, fos);
            }
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(myPath);

    }

    @Test
    public void test_Image_module_rounded_corners() {
        imagemodule = new CHImageModule(context);
        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res/blurred.png";
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        assertNotNull(CHImageModule.getRoundedCornerBitmap(bitmap, 15));
    }

    @Test
    public void test_Image_module_get_resized_bitmap() {
        imagemodule = new CHImageModule(context);
        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res/blurred.png";
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        assertNotNull(imagemodule.getResizedBitmap(bitmap, 150, 150));

    }



}
