package com.example.streamingapp.data.model;

import java.util.Objects;

public class FavouriteItem {

    public final ContentType type;
    public final Object data;

    public FavouriteItem(ContentType type, Object data) {
        this.type = type;
        this.data = data;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FavouriteItem)) return false;

        FavouriteItem other = (FavouriteItem) o;

        if (type != other.type) return false;

        if (type == ContentType.MOVIE) {
            return ((MovieItems) data).getId()
                    == ((MovieItems) other.data).getId();
        } else {
            return ((SeriesItems) data).getId()
                    == ((SeriesItems) other.data).getId();
        }
    }

    @Override
    public int hashCode() {
        if (type == ContentType.MOVIE) {
            return Objects.hash(type, ((MovieItems) data).getId());
        } else {
            return Objects.hash(type, ((SeriesItems) data).getId());
        }
    }
}


