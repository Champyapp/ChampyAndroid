import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;

import com.azinecllc.champy.BuildConfig;
import com.azinecllc.champy.activity.FriendsActivity;
import com.azinecllc.champy.helper.CHImageModule;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.FileNotFoundException;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertNotNull;

/**
 * Created by ivan on 04.01.16.
 */

@Config(constants = BuildConfig.class, sdk = Build.VERSION_CODES.LOLLIPOP)
@RunWith(RobolectricGradleTestRunner.class)
public class Image_module {
    private CHImageModule imagemodule;
    private Context context;
    private FriendsActivity activity;

    @Before
    public void set_up() throws Exception {
        context = RuntimeEnvironment.application;
    }

    @Test
    public void test_Image_module_init() throws URISyntaxException, FileNotFoundException {
        imagemodule = new CHImageModule(context);
        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res";
        Drawable dr = imagemodule.Init(filePath, null);
        assertNotNull("Ok!!", dr);
    }

    @Test
    public void test_Image_module_rounded_corners() {
        imagemodule = new CHImageModule(context);
        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res/blured2.jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        assertNotNull(imagemodule.getRoundedCornerBitmap(bitmap, 15));
    }

    @Test
    public void test_Image_module_get_resized_bitmap() {
        imagemodule = new CHImageModule(context);
        String filePath = RuntimeEnvironment.application.getPackageResourcePath() + "/src/test/res/blured2.jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        assertNotNull(imagemodule.getResizedBitmap(bitmap, 150, 150));

    }



}
