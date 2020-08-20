package io.mydomain.ratingsdataservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class UserRating {

    private List<Rating> userRating;

    public UserRating() {
    }

    public UserRating(List<Rating> userRating) {
        this.userRating = userRating;
    }

    public List<Rating> getUserRating() {
        return userRating;
    }

    public void setUserRating(List<Rating> userRating) {
        this.userRating = userRating;
    }
}
