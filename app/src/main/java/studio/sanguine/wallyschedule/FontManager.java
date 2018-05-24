package studio.sanguine.wallyschedule;

import android.content.Context;
import android.graphics.Typeface;

/**
 * Created by mafia on 11/17/2017.
 */

public class FontManager {

        public static final String ROOT = "fonts/",
                FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

        public static Typeface getTypeface(Context context, String font) {
            return Typeface.createFromAsset(context.getAssets(), font);
        }
}
