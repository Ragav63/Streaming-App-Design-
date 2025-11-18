package com.example.streamingapp.data.local;

import com.example.streamingapp.R;
import com.example.streamingapp.data.model.AboutPhotosItems;
import com.example.streamingapp.data.model.CastItems;
import com.example.streamingapp.data.model.CategoryItems;
import com.example.streamingapp.data.model.CountryItems;
import com.example.streamingapp.data.model.DownloadItems;
import com.example.streamingapp.data.model.HistoryItems;
import com.example.streamingapp.data.model.MovieItems;
import com.example.streamingapp.data.model.PickGenreTypeRecItem;
import com.example.streamingapp.data.model.PickVideoTypeRecItem;
import com.example.streamingapp.data.model.SeasonItems;
import com.example.streamingapp.data.model.SeriesItems;
import com.example.streamingapp.data.model.TrailerItems;
import com.example.streamingapp.data.model.TvItems;

import java.util.ArrayList;
import java.util.List;

public class StreamingDataSource {
    public List<PickGenreTypeRecItem> getGenreList() {
        List<PickGenreTypeRecItem> itemList = new ArrayList<>();
        itemList.add(new PickGenreTypeRecItem(R.drawable.spartans1,"Action"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.strangerthings1,"Adventure"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.sports1,"Biography"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.incedible,"Comedy"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.tvshows1,"Crime"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.spartans1,"Documentry"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.strangerthings1,"Drama"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.sports1,"Family"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.incedible,"Fantasy"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.tvshows1,"History"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.spartans1,"Horror"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.strangerthings1,"Mystery"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.sports1,"Romance"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.incedible,"Scifi"));
        itemList.add(new PickGenreTypeRecItem(R.drawable.tvshows1,"Thriller"));

        return itemList;
    }

    public List<CastItems> getCastList() {
        List<CastItems> itemList = new ArrayList<>();
        itemList.add(new CastItems("Sam Worthington", "Actor", R.drawable.samworthington));
        itemList.add(new CastItems("Zoe Saldana", "Actor",R.drawable.zoesaldana));
        itemList.add(new CastItems("Michele Rodriguez", "Actor",R.drawable.michelerodriguez));
        itemList.add(new CastItems("Sigourney Weaver", "Actor",R.drawable.sigourneyweaver));
        itemList.add(new CastItems("Stephen Lang", "Actor",R.drawable.stephenlang));
        return itemList;
    }

    public List<AboutPhotosItems> getPhotosList() {
        List<AboutPhotosItems> photoList = new ArrayList<>();
        photoList.add(new AboutPhotosItems(R.drawable.avatarhz));
        photoList.add(new AboutPhotosItems(R.drawable.avatarhz1));
        photoList.add(new AboutPhotosItems(R.drawable.avatarhz2));
        photoList.add(new AboutPhotosItems(R.drawable.avatarhz3));
        photoList.add(new AboutPhotosItems(R.drawable.avatarhz4));
        return photoList;
    }

    public List<MovieItems> getMoviesList() {
        List<MovieItems> itemList = new ArrayList<>();
        itemList.add(new MovieItems("7.2", "Venom 3", "2018", "Fantasy", "USA", "2h 45m", "A failed reporter is bonded to an alien entity, one of many symbiotes who have invaded Earth. But the being takes a liking to Earth and decides to protect it.", R.drawable.venom3verticalnew));
        itemList.add(new MovieItems("6.8", "Kalki", "2024", "History", "India", "2h 40m", "A modern-day avatar of Vishnu, a Hindu god, who is believed to have descended to earth to protect the world from evil forces.",R.drawable.kalkiverticalnew));
        itemList.add(new MovieItems("8.0", "Avengers Endgame", "2019", "Action", "USA", "3h 05m", "After the devastating events of Avengers: Infinity War (2018), the universe is in ruins. With the help of remaining allies, the Avengers assemble once more in order to reverse Thanos' actions and restore balance to the universe.",R.drawable.avengersverticalnew));
        itemList.add(new MovieItems("7.0", "Avatar The Last Airbender", "2023", "History", "USA", "2h 35m", "A young boy known as the Avatar must master the four elemental powers to save the world, and fight against an enemy bent on stopping him.",R.drawable.avatarthelastairbenderverticalnew));
        itemList.add(new MovieItems("7.3", "Captain America", "2017", "Action", "USA", "2h 27m", "Political involvement in the Avengers' affairs causes a rift between Captain America and Iron Man.",R.drawable.captainamericaverticalnew));
        itemList.add(new MovieItems("6.5", "Avatar The Way of Water", "2023", "Fantasy", "USA", "3h 45m", "Jake Sully lives with his newfound family on the extrasolar moon Pandora. Once a familiar threat returns to finish what was previously started. Jake must work with Neytiri and the army of the Navi race to protect their home.",R.drawable.avatarthewayofwaterverticalnew1));

        return itemList;
    }

    public List<SeriesItems> getSeriesList() {
        List<SeriesItems> itemList = new ArrayList<>();
        itemList.add(new SeriesItems("7.2", "Game of thrones", "2011", "Action", "USA", "8", "Nine noble families fight for control over the lands of Westeros, while an ancient enemy returns after being dormant for millennia.", R.drawable.got));
        itemList.add(new SeriesItems("6.8", "Dark", "2017", "Crime", "USA", "3", "A family saga with a supernatural twist, set in a German town where the disappearance of two young children exposes the relationships among four families.", R.drawable.dark));
        itemList.add(new SeriesItems("8.0", "The Boys", "2019", "Dark Comedy", "USA", "4", "A group of vigilantes set out to take down corrupt superheroes who abuse their superpowers.", R.drawable.theboys));
        itemList.add(new SeriesItems("7.0", "The 100", "2014", "Scifi - Drama", "USA", "7", "Set 97 years after a nuclear war destroyed civilization, when a spaceship housing humanity's lone survivors sends 100 juvenile delinquents back to Earth, hoping to repopulate the planet.", R.drawable.the100));
        itemList.add(new SeriesItems("7.3", "Breaking Bad", "2008", "Crime", "USA", "5", "A chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine with a former student in order to secure his family's future.", R.drawable.brbanew));
        itemList.add(new SeriesItems("6.5", "Prison Break", "2005", "Prison Drama", "USA", "5", "A structural engineer installs himself in a prison he helped design, in order to save his falsely accused brother from a death sentence by breaking themselves out from the inside.", R.drawable.prisonbreakverticalnew));

        return itemList;
    }

    public List<CategoryItems> getCategories() {
        List<CategoryItems> itemList = new ArrayList<>();
        itemList.add(new CategoryItems("TV CHANNELS", R.drawable.strthings));
        itemList.add(new CategoryItems("MOVIES", R.drawable.spartans));
        itemList.add(new CategoryItems("CARTOONS", R.drawable.anime));
        itemList.add(new CategoryItems("SCI-FI", R.drawable.scifi));
        itemList.add(new CategoryItems("SPORT", R.drawable.sports));
        itemList.add(new CategoryItems("SERIES", R.drawable.strthings));
        itemList.add(new CategoryItems("TV SHOWS", R.drawable.tvshows));

        return itemList;
    }

    public List<CountryItems> getCountriesList() {
        List<CountryItems> itemsList = new ArrayList<>();
        itemsList.add(new CountryItems("All"));
        itemsList.add(new CountryItems("India"));
        itemsList.add(new CountryItems("USA"));
        itemsList.add(new CountryItems("Korea"));
        itemsList.add(new CountryItems("China"));

        return itemsList;
    }

    public List<DownloadItems> getDownloadItemsList(){
        List<DownloadItems> itemsList = new ArrayList<>();
        itemsList.add(new DownloadItems("Avatar: The Way of Water", "","1h 34min / 7.2 gb / 720p", R.drawable.avatarthewayofwatervertical));
        itemsList.add(new DownloadItems("Game of Thrones, Season 1", "Episode 1, Winter is Coming","1h 34min / 7.2 gb / 720p", R.drawable.got));

        return itemsList;
    }

    public List<HistoryItems> getHistoryItemList(){
        List<HistoryItems> itemsList = new ArrayList<>();
        itemsList.add(new HistoryItems("7.3","View 10.06.2024", R.drawable.venom3verticalnew));
        itemsList.add(new HistoryItems("7.0","View 06.06.2024", R.drawable.avatarthelastairbenderverticalnew));
        itemsList.add(new HistoryItems("8.0","View 10.05.2024", R.drawable.avengersverticalnew));
        itemsList.add(new HistoryItems("6.5","View 01.05.2024", R.drawable.avatarthewayofwaterverticalnew1));
        itemsList.add(new HistoryItems("7.2","View 28.04.2024", R.drawable.kalkiverticalnew));
        itemsList.add(new HistoryItems("7.3","View 25.04.2024", R.drawable.captainamericaverticalnew));

        return itemsList;
    }




    public List<PickVideoTypeRecItem> getVideoTypeList() {
        List<PickVideoTypeRecItem> itemList = new ArrayList<>();
        itemList.add(new PickVideoTypeRecItem(R.drawable.spartans1,"Movies"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.strangerthings1,"Series"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.sports1,"Sports"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.incedible,"Cartoons"));
        itemList.add(new PickVideoTypeRecItem(R.drawable.tvshows1,"Tv Shows"));

        return itemList;
    }

    public List<SeasonItems> getSeasonItemsList() {
        List<SeasonItems> itemList = new ArrayList<>();
        itemList.add(new SeasonItems(R.drawable.gots01e01, "1. Valar Doharaeis", "55 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e02, "2. Dark Wings, Dark Woods", "52 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e03, "3. Lord Snow", "1 hour 12 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e04, "4. Cripples, Bastards and Broken Things", "1 hour 17 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e05, "5. The Wolf and the Lion", "1 hour 5 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e06, "6. The Kingsguard", "1 hour 12 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e07, "7. A Golden Crown", "1 hour 1 min"));
        itemList.add(new SeasonItems(R.drawable.gots01e08, "8. Winter is Coming", "1 hour 12 min"));
        return itemList;
    }

    public List<TrailerItems> getTrailersList() {
        List<TrailerItems> itemList = new ArrayList<>();
        itemList.add(new TrailerItems("Avatar: The Way of Water |Official Teaser Trailer", "2 min 14 sec", R.drawable.avatarthewayofwater));
        itemList.add(new TrailerItems("Avatar: The Way of Water |New Trailer", "2 min 14 sec",R.drawable.avatarthewayofwater));
        itemList.add(new TrailerItems("Avatar: The Way of Water |Trailer 2024", "2 min 14 sec",R.drawable.avatarthewayofwater));
        return itemList;
    }

    public List<TvItems> getTvList() {
        List itemsList = new ArrayList<>();
        itemsList.add(new TvItems("espn","ESPN", "NBA Playoff: Lakers vs Denver, Game 2","11.35-12.50",R.drawable.spart));
        itemsList.add(new TvItems("abc","ABC", "Euphoria - Season 1, Episode 1","12.35-01.50",R.drawable.strthings));
        itemsList.add(new TvItems("fox","FOX", "Shogun - Season 1, Episode 3","11.35-12.50",R.drawable.scifi1));
        itemsList.add(new TvItems("abc","abc", "High School Musical","11.35-12.50",R.drawable.scifi1));
        itemsList.add(new TvItems("SS","ss", "CSK vs MI","11.35-12.50",R.drawable.scifi1));
        itemsList.add(new TvItems("SS","ss", "KKR vs Delhi","11.35-12.50",R.drawable.scifi1));

        return itemsList;
    }


}
