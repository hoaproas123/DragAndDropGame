package com.example.smid;

import android.graphics.Color;
/**
 * A class containing constants used for the game's user interface.
 *
 * @author Hồ Ngọc Hòa, Phạm Nguyễn Hoài Nam
 * @version 2010.1105
 * @since 1.0
 */
public class SettingUI {
    /**
    Màu nền mặc định của các ô vuông. */ public static final int colorDefault = Color.parseColor("#E191A3");

    /**
     Màu nền khi người chơi kéo hình ảnh chính vào ô vuông. */ public static final int colorChange = Color.RED;

    /**
     Hệ số tỷ lệ tối thiểu của hình ảnh chính. */ public static final int minScaleX = 1;

    /**
     Hệ số tỷ lệ tối thiểu của hình ảnh chính. */ public static final int minScaleY = 1;

    /**
     Hệ số tỷ lệ tối đa của hình ảnh chính. */ public static final int maxScaleX = 3;

    /**
     Hệ số tỷ lệ tối đa của hình ảnh chính. */ public static final int maxScaleY = 3;

    /**
     Thời gian đếm ngược mặc định. */ public static final int timeCountdownDefault = 30;
}


