package cn.aigestudio.datepicker.bizs.themes;

/**
 * 主题的默认实现类
 * 
 * The default implement of theme
 *
 * @author AigeStudio 2015-06-17
 */
public class DPBaseTheme extends DPTheme {
    @Override
    public int colorBG() {
        return 0xFFFFFFFF;
    }

    @Override
    public int colorBGCircle() {
        return 0x44000000;
    }

    @Override
    public int colorTitleBG() {
        return 0xFFffffff;
    }

    @Override
    public int colorTitle() {
        return 0xEE333333;
    }

    @Override
    public int colorToday() {
        return 0x3000a8ff;
    }

    @Override
    public int colorG() {
        return 0xEE333333;
    }

    @Override
    public int colorF() {
        return 0xffc4c4c4;
    }

    @Override
    public int colorWeekend() {
        return 0xffc4c4c4;
    }

    @Override
    public int colorHoliday() {
        return 0x80FED6D6;
    }
}
