package co.hani.myket.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GameModel implements Parcelable {
    public GameModel() {
    }
    private String title;
    private String categoryName;
    private String iconPath;
    private float rating;





    protected GameModel(Parcel in) {
        setTitle(in.readString());
        setCategoryName(in.readString());
        setIconPath(in.readString());
        setRating(in.readFloat());
    }

    public static final Creator<GameModel> CREATOR = new Creator<GameModel>() {
        @Override
        public GameModel createFromParcel(Parcel in) {
            return new GameModel(in);
        }

        @Override
        public GameModel[] newArray(int size) {
            return new GameModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }


    public static Creator<GameModel> getCREATOR() {
        return CREATOR;
    }



    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getTitle());
        dest.writeString(getCategoryName());
        dest.writeString(getIconPath());
        dest.writeFloat(getRating());
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }


    public static final class KEY {
        public static final String ID_KEY = "id";
        public static final String TITLE = "title";
        public static final String CATEGORY_NAME = "categoryName";
        public static final String ICON_PATH = "iconPath";
        public static final String RATING = "rating";

    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getIconPath() {
        return iconPath;
    }

    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

}