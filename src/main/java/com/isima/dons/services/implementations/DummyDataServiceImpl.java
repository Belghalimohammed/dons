package com.isima.dons.services.implementations;
import com.isima.dons.services.DummyDataService;  // Import DummyDataService
import com.isima.dons.entities.Annonce;  // Import Annonce entity
import com.isima.dons.entities.User;  // Import User entity
import com.isima.dons.repositories.AnnonceRepository;  // Import AnnonceRepository
import com.isima.dons.repositories.UserRepository;  // Import UserRepository
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Service
public class DummyDataServiceImpl implements DummyDataService {

    private final AnnonceRepository annonceRepository;
    private final UserRepository userRepository;

    @Autowired
    public DummyDataServiceImpl(AnnonceRepository annonceRepository, UserRepository userRepository) {
        this.annonceRepository = annonceRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void generateDummyData() {
        Faker faker = new Faker();
        int numUsers = 10;  // Number of users
        int numAnnonces = 5;  // Number of annonces per user

        // Create dummy users (10 users)
        for (int i = 0; i < numUsers; i++) {
            User user = new User();
            user.setUsername(faker.name().username());
            user.setEmail(faker.internet().emailAddress());
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);
            String password = faker.internet().password();
            user.setPassword(encoder.encode(password));
            userRepository.save(user);

            // Create dummy Annonce data for each user
            for (int j = 0; j < numAnnonces; j++) {
                Annonce annonce = new Annonce();

                // More logical titles related to products, services, or offers
                annonce.setTitre(generateProductTitle(faker));

                // Generate a more meaningful description
                annonce.setDescription(generateProductDescription(faker));

                // Set a realistic état (condition) for the object
                annonce.setEtatObjet(Annonce.EtatObjet.values()[faker.random().nextInt(Annonce.EtatObjet.values().length)]);

                // Set a random but realistic publication date
                annonce.setDatePublication(LocalDate.now().minusDays(faker.random().nextInt(30)));


                annonce.setZone(generateProductZone(faker));

                // Set random donation type (true/false)
                annonce.setTypeDon(faker.random().nextBoolean());

                // Associate the user with the Annonce
                annonce.setVendeur(user);

                // Generate relevant keywords for the annonce
                annonce.setKeywords(generateKeywords(faker));
                annonce.setPri(false);

                annonceRepository.save(annonce);
            }
        }
    }

    // Generate more logical product/service titles
    private String generateProductTitle(Faker faker) {
        String[] titles = {
            "Smartphone " + faker.company().name(),
            "Vintage Leather Sofa",
            "Electric Bicycle",
            "Gaming Laptop - " + faker.company().bs(),
            "Used Bicycle for Sale"
        };
        return titles[faker.random().nextInt(titles.length)];
    }

    private String generateProductZone(Faker faker) {
        String[] zones = {
                "Île-de-France",
                "Provence-Alpes-Côte d'Azur",
                "Auvergne-Rhône-Alpes",
                "Nouvelle-Aquitaine",
                "Occitanie",
                "Hauts-de-France",
                "Grand Est",
                "Bretagne",
                "Normandie",
                "Pays de la Loire",
                "Bourgogne-Franche-Comté",
                "Centre-Val de Loire",
                "Corse",
                "Martinique",
                "Guadeloupe",
                "La Réunion",
                "Guyane",
                "Mayotte"
        };
        return zones[faker.random().nextInt(zones.length)];
    }


    // Generate more logical descriptions related to products or services
    private String generateProductDescription(Faker faker) {
        return "A high-quality " + faker.commerce().productName() + " that offers great value for money. "
                + "Perfect for anyone looking for " + faker.commerce().material() + " and exceptional performance. "
                + "This " + faker.commerce().color() + " item is ideal for daily use or as a special gift.";
    }

    private List<String> generateKeywords(Faker faker) {
        return Arrays.asList(
              // Random word as an adjective (generic)
            faker.commerce().material(),  // Material (e.g., "wood", "plastic")
            faker.commerce().department() // Department (e.g., "Electronics", "Home Goods")
        );
    }



    

}
