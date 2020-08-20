package io.mydomain.moviecatalogservice;

import io.mydomain.moviecatalogservice.models.CatalogItem;
import io.mydomain.moviecatalogservice.models.Movie;
import io.mydomain.moviecatalogservice.models.Rating;
import io.mydomain.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient.Builder builder;

    //when a userid is given for the Movie Catalog Service Api
    // get all rated movieIDs by the user
    // for each movie ID, call movie info service to get movie details (i.e. name in our case)
    // put them together

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        //1. Let's hardcode the movies the user watched and the ratings assigned for now
        /*
        List<Rating> ratings = Arrays.asList(
                new Rating("1234", 4),
                new Rating("5678", 3)
        );
        */

        //Before adding the Eureka service:
        // 1. Ok change the hardcoded option above to an API call, since we are dealing with microservices
        // So step 1, get the UserRatings given UserId
//        UserRating ratings = restTemplate.getForObject("http://localhost:8083/ratingsdata/users/"+ userId, UserRating.class);

        //After adding the Eureka service:
        //The 'ratings-data-service' is the application name stated in the respective microservice's application.properties file
        //Also found in eureka service dashboard (localhost:Eurekaport)
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/"+ userId, UserRating.class);


        //2. Loop thru the response we received from step 1.
        return ratings.getUserRating().stream().map(rating -> {
                    //originally when it was hardcoded:
                    //rating -> new CatalogItem("Transformers", "Test Desc", rating.getRating())

                    //Synchronous Option:
                    // ratings object format from step 1 is:
                    // {"userRating":[{"movieId":"1234","rating":4},{"movieId":"5678","rating":3}]}, that is coming from 8083/ratingsdata/users/:userid
                    // for each object in the array we have a movieId
                    //therefore we call the movies Microservice using restTemplate object, using the movieIds from userRating object
                    //then inject into a CatalogItem object for each movie response
                    Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);

                    //Async Option:

                    //use builder pattern to build
                    //whats the method? In this case .get()
                    //where to fetch from? what's the uri?
                    //ok go fetch now (retrieve()
                    //bodyToMono is equivalent of Promise in javascript
                    //and block it until I get the response... (so why async?)
                    // Movie movie = builder.build().get().uri("http://localhost:8081/movies/" + rating.getMovieId()).retrieve().bodyToMono(Movie.class).block();

                    return new CatalogItem(movie.getName(), "movie description, hardcoded", rating.getRating());
                }
        ).collect(Collectors.toList());

       /*
       This code was first introduced to show that our rest endpoint was working
       Depracated with the introduction of ratings.stream

        return Collections.singletonList(
                new CatalogItem("Transformers", "Test desc", 4)
        );
        */
    }
}

