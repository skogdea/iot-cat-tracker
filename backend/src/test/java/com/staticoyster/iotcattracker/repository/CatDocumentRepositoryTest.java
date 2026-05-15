package com.staticoyster.iotcattracker.repository;

import com.opentable.db.postgres.embedded.EmbeddedPostgres;
import com.staticoyster.iotcattracker.enums.CatBreed;
import com.staticoyster.iotcattracker.enums.CatColorPattern;
import com.staticoyster.iotcattracker.enums.CatGender;
import com.staticoyster.iotcattracker.enums.CatPersonality;
import com.staticoyster.iotcattracker.model.CatDocumentModel;
import com.staticoyster.iotcattracker.model.FavoriteFoodModel;
import com.staticoyster.iotcattracker.model.FavoriteToyModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import javax.sql.DataSource;
import org.assertj.core.api.Assertions;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CatDocumentRepositoryTest {

    @Autowired
    private CatDocumentRepository catDocumentRepository;

    @Autowired
    private DataSource dataSource;

    private static final EmbeddedPostgres embeddedPostgres;

    private List<CatDocumentModel> catDocumentModels;

    static {
        try {
            embeddedPostgres = EmbeddedPostgres.builder().start();
        } catch (Exception exception) {
            throw new RuntimeException("Failed to start embedded Postgres", exception);
        }
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        try {
            // Use the JDBC URL provided by the EmbeddedPostgres instance
            String jdbcUrl = embeddedPostgres.getJdbcUrl("postgres");
            registry.add("spring.datasource.url", () -> jdbcUrl);
            registry.add("spring.datasource.username", () -> "postgres");
            registry.add("spring.datasource.password", () -> "postgres");
            registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
            registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.PostgreSQLDialect");
        } catch (Exception exception) {
            throw new RuntimeException("Failed to configure data source properties", exception);
        }
    }

    @BeforeEach
    public void before() {
        // Run Flyway migrations manually
        Flyway flyway = Flyway.configure().dataSource(dataSource).load();
        flyway.migrate();

        List<FavoriteFoodModel> meiMeiFavoriteFoods;
        List<FavoriteFoodModel> caesarFavoriteFoods;
        List<FavoriteToyModel> meiMeiFavoriteToys;
        List<FavoriteToyModel> caesarFavoriteToys;

        FavoriteFoodModel meiMeiFavoriteFood = new FavoriteFoodModel();
        meiMeiFavoriteFood.setFavoriteFood("freeze dried");

        meiMeiFavoriteFoods = new ArrayList<>();
        meiMeiFavoriteFoods.add(meiMeiFavoriteFood);

        FavoriteFoodModel caesarFavoriteFood = new FavoriteFoodModel();
        caesarFavoriteFood.setFavoriteFood("chicken");

        caesarFavoriteFoods = new ArrayList<>();
        caesarFavoriteFoods.add(caesarFavoriteFood);

        FavoriteToyModel meiMeiFavoriteToy = new FavoriteToyModel();
        meiMeiFavoriteToy.setFavoriteToy("hair tie");

        meiMeiFavoriteToys = new ArrayList<>();
        meiMeiFavoriteToys.add(meiMeiFavoriteToy);

        FavoriteToyModel caesarFavoriteToy = new FavoriteToyModel();
        caesarFavoriteToy.setFavoriteToy("twig");

        caesarFavoriteToys = new ArrayList<>();
        caesarFavoriteToys.add(caesarFavoriteToy);

        FavoriteToyModel caesarFavoriteToy2 = new FavoriteToyModel();
        caesarFavoriteToy2.setFavoriteToy("catnip");
        caesarFavoriteToys.add(caesarFavoriteToy2);

        catDocumentModels = Arrays.asList(
                CatDocumentModel.Builder.newBuilder()
                        .withCatName("mei mei")
                        .withCatGender(CatGender.FEMALE)
                        .withCatAge(2)
                        .withCatBreed(CatBreed.MIX)
                        .withCatColorPattern(CatColorPattern.TORTOISESHELL)
                        .withCatPersonality(CatPersonality.LIVELY)
                        .withFavoriteFoods(meiMeiFavoriteFoods)
                        .withFavoriteToys(meiMeiFavoriteToys)
                        .withBestFriend("her younger brother")
                        .build(),
                CatDocumentModel.Builder.newBuilder()
                        .withCatName("caesar")
                        .withCatGender(CatGender.MALE)
                        .withCatAge(9)
                        .withCatBreed(CatBreed.MIX)
                        .withCatColorPattern(CatColorPattern.GINGER)
                        .withCatPersonality(CatPersonality.SEDENTARY)
                        .withFavoriteFoods(caesarFavoriteFoods)
                        .withFavoriteToys(caesarFavoriteToys)
                        .withBestFriend("my mom")
                        .build());
    }

    @Test
    public void saveOneShouldReturnTheSameData() {
        CatDocumentModel oneToBeSaved = catDocumentModels.get(0);
        CatDocumentModel oneSaved = catDocumentRepository.save(oneToBeSaved);

        Assertions.assertThat(oneSaved.getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(oneSaved.getCatGender())
                .isEqualTo(catDocumentModels.get(0).getCatGender());
        Assertions.assertThat(oneSaved.getCatAge())
                .isEqualTo(catDocumentModels.get(0).getCatAge());
        Assertions.assertThat(oneSaved.getCatBreed())
                .isEqualTo(catDocumentModels.get(0).getCatBreed());
        Assertions.assertThat(oneSaved.getCatColorPattern())
                .isEqualTo(catDocumentModels.get(0).getCatColorPattern());
        Assertions.assertThat(oneSaved.getCatPersonality())
                .isEqualTo(catDocumentModels.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(oneSaved);
        Assertions.assertThat(oneSaved.getBestFriend())
                .isEqualTo(catDocumentModels.get(0).getBestFriend());
    }

    @Test
    public void findByIdWhenFoundShouldBeRetrieved() {
        catDocumentRepository.saveAll(catDocumentModels);
        Optional<CatDocumentModel> oneRequested =
                catDocumentRepository.findById(catDocumentModels.get(0).getCatId());

        Assertions.assertThat(oneRequested).isPresent();
        Assertions.assertThat(oneRequested.get().getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(oneRequested.get().getCatGender())
                .isEqualTo(catDocumentModels.get(0).getCatGender());
        Assertions.assertThat(oneRequested.get().getCatAge())
                .isEqualTo(catDocumentModels.get(0).getCatAge());
        Assertions.assertThat(oneRequested.get().getCatBreed())
                .isEqualTo(catDocumentModels.get(0).getCatBreed());
        Assertions.assertThat(oneRequested.get().getCatColorPattern())
                .isEqualTo(catDocumentModels.get(0).getCatColorPattern());
        Assertions.assertThat(oneRequested.get().getCatPersonality())
                .isEqualTo(catDocumentModels.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(oneRequested.get());
        Assertions.assertThat(oneRequested.get().getBestFriend())
                .isEqualTo(catDocumentModels.get(0).getBestFriend());
    }

    @Test
    public void findByIdWhenNotFoundShouldReturnEmpty() {
        UUID catId = UUID.randomUUID();
        Optional<CatDocumentModel> oneRequested = catDocumentRepository.findById(catId);
        Assertions.assertThat(oneRequested).isEqualTo(Optional.empty());
    }

    @Test
    public void findByNameWhenFoundShouldBeRetrieved() {
        catDocumentRepository.saveAll(catDocumentModels);
        Optional<CatDocumentModel> oneRequested =
                catDocumentRepository.findByCatName(catDocumentModels.get(1).getCatName());

        Assertions.assertThat(oneRequested).isPresent();
        Assertions.assertThat(oneRequested.get().getCatName())
                .isEqualTo(catDocumentModels.get(1).getCatName());
        Assertions.assertThat(oneRequested.get().getCatGender())
                .isEqualTo(catDocumentModels.get(1).getCatGender());
        Assertions.assertThat(oneRequested.get().getCatAge())
                .isEqualTo(catDocumentModels.get(1).getCatAge());
        Assertions.assertThat(oneRequested.get().getCatBreed())
                .isEqualTo(catDocumentModels.get(1).getCatBreed());
        Assertions.assertThat(oneRequested.get().getCatColorPattern())
                .isEqualTo(catDocumentModels.get(1).getCatColorPattern());
        Assertions.assertThat(oneRequested.get().getCatPersonality())
                .isEqualTo(catDocumentModels.get(1).getCatPersonality());
        assertListOfCaesarFoodsAndToys(oneRequested.get());
        Assertions.assertThat(oneRequested.get().getBestFriend())
                .isEqualTo(catDocumentModels.get(1).getBestFriend());
    }

    @Test
    public void findByNameWhenNotFoundShouldReturnEmpty() {
        String catName = "SolNa";
        Optional<CatDocumentModel> oneRequested = catDocumentRepository.findByCatName(catName);
        Assertions.assertThat(oneRequested).isEqualTo(Optional.empty());
    }

    @Test
    public void findByIdsWhenFoundShouldBeRetrieved() {
        catDocumentRepository.saveAll(catDocumentModels);
        List<CatDocumentModel> allRequested = catDocumentRepository.findByCatIdIn(Set.of(
                catDocumentModels.get(0).getCatId(), catDocumentModels.get(1).getCatId()));
        Assertions.assertThat(allRequested).hasSize(2);
        Assertions.assertThat(allRequested.get(0).getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(allRequested.get(0).getCatGender())
                .isEqualTo(catDocumentModels.get(0).getCatGender());
        Assertions.assertThat(allRequested.get(0).getCatAge())
                .isEqualTo(catDocumentModels.get(0).getCatAge());
        Assertions.assertThat(allRequested.get(0).getCatBreed())
                .isEqualTo(catDocumentModels.get(0).getCatBreed());
        Assertions.assertThat(allRequested.get(0).getCatColorPattern())
                .isEqualTo(catDocumentModels.get(0).getCatColorPattern());
        Assertions.assertThat(allRequested.get(0).getCatPersonality())
                .isEqualTo(catDocumentModels.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(allRequested.get(0));
        Assertions.assertThat(allRequested.get(0).getBestFriend())
                .isEqualTo(catDocumentModels.get(0).getBestFriend());

        Assertions.assertThat(allRequested.get(1).getCatName())
                .isEqualTo(catDocumentModels.get(1).getCatName());
        Assertions.assertThat(allRequested.get(1).getCatGender())
                .isEqualTo(catDocumentModels.get(1).getCatGender());
        Assertions.assertThat(allRequested.get(1).getCatAge())
                .isEqualTo(catDocumentModels.get(1).getCatAge());
        Assertions.assertThat(allRequested.get(1).getCatBreed())
                .isEqualTo(catDocumentModels.get(1).getCatBreed());
        Assertions.assertThat(allRequested.get(1).getCatColorPattern())
                .isEqualTo(catDocumentModels.get(1).getCatColorPattern());
        Assertions.assertThat(allRequested.get(1).getCatPersonality())
                .isEqualTo(catDocumentModels.get(1).getCatPersonality());
        assertListOfCaesarFoodsAndToys(allRequested.get(1));
        Assertions.assertThat(allRequested.get(1).getBestFriend())
                .isEqualTo(catDocumentModels.get(1).getBestFriend());
    }

    @Test
    public void findByIdsWhenNotFoundShouldReturnEmpty() {
        Set<UUID> catIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        List<CatDocumentModel> modelsRequested = catDocumentRepository.findByCatIdIn(catIds);
        Assertions.assertThat(modelsRequested).hasSize(0).isEmpty();
    }

    @Test
    public void findByNamesWhenFoundShouldBeRetrieved() {
        catDocumentRepository.saveAll(catDocumentModels);
        List<CatDocumentModel> allRequested = catDocumentRepository.findByCatNameIn(List.of(
                catDocumentModels.get(0).getCatName(), catDocumentModels.get(1).getCatName()));
        Assertions.assertThat(allRequested).hasSize(2);
        Assertions.assertThat(allRequested.get(0).getCatName())
                .isEqualTo(catDocumentModels.get(0).getCatName());
        Assertions.assertThat(allRequested.get(0).getCatGender())
                .isEqualTo(catDocumentModels.get(0).getCatGender());
        Assertions.assertThat(allRequested.get(0).getCatAge())
                .isEqualTo(catDocumentModels.get(0).getCatAge());
        Assertions.assertThat(allRequested.get(0).getCatBreed())
                .isEqualTo(catDocumentModels.get(0).getCatBreed());
        Assertions.assertThat(allRequested.get(0).getCatColorPattern())
                .isEqualTo(catDocumentModels.get(0).getCatColorPattern());
        Assertions.assertThat(allRequested.get(0).getCatPersonality())
                .isEqualTo(catDocumentModels.get(0).getCatPersonality());
        assertListOfMeiMeiFoodsAndToys(allRequested.get(0));
        Assertions.assertThat(allRequested.get(0).getBestFriend())
                .isEqualTo(catDocumentModels.get(0).getBestFriend());

        Assertions.assertThat(allRequested.get(1).getCatName())
                .isEqualTo(catDocumentModels.get(1).getCatName());
        Assertions.assertThat(allRequested.get(1).getCatGender())
                .isEqualTo(catDocumentModels.get(1).getCatGender());
        Assertions.assertThat(allRequested.get(1).getCatAge())
                .isEqualTo(catDocumentModels.get(1).getCatAge());
        Assertions.assertThat(allRequested.get(1).getCatBreed())
                .isEqualTo(catDocumentModels.get(1).getCatBreed());
        Assertions.assertThat(allRequested.get(1).getCatColorPattern())
                .isEqualTo(catDocumentModels.get(1).getCatColorPattern());
        Assertions.assertThat(allRequested.get(1).getCatPersonality())
                .isEqualTo(catDocumentModels.get(1).getCatPersonality());
        assertListOfCaesarFoodsAndToys(allRequested.get(1));
        Assertions.assertThat(allRequested.get(1).getBestFriend())
                .isEqualTo(catDocumentModels.get(1).getBestFriend());
    }

    @Test
    public void findByNamesWhenNotFoundShouldReturnEmpty() {
        List<String> catNames = List.of("SolNa", "ZonGzi");
        List<CatDocumentModel> modelRequested = catDocumentRepository.findByCatNameIn(catNames);
        Assertions.assertThat(modelRequested).hasSize(0).isEmpty();
    }

    private void assertListOfMeiMeiFoodsAndToys(CatDocumentModel model) {
        List<FavoriteFoodModel> foods = model.getFavoriteFoods();
        Assertions.assertThat(foods)
                .hasSize(catDocumentModels.get(0).getFavoriteFoods().size());
        Assertions.assertThat(foods)
                .usingElementComparator(Comparator.comparing(FavoriteFoodModel::getFavoriteFood))
                .containsExactlyElementsOf(catDocumentModels.get(0).getFavoriteFoods());
        List<FavoriteToyModel> toys = model.getFavoriteToys();
        Assertions.assertThat(toys)
                .hasSize(catDocumentModels.get(0).getFavoriteToys().size());
        Assertions.assertThat(toys)
                .usingElementComparator(Comparator.comparing(FavoriteToyModel::getFavoriteToy))
                .containsExactlyElementsOf(catDocumentModels.get(0).getFavoriteToys());
    }

    private void assertListOfCaesarFoodsAndToys(CatDocumentModel model) {
        List<FavoriteFoodModel> foods = model.getFavoriteFoods();
        Assertions.assertThat(foods)
                .hasSize(catDocumentModels.get(1).getFavoriteFoods().size());
        Assertions.assertThat(foods)
                .usingElementComparator(Comparator.comparing(FavoriteFoodModel::getFavoriteFood))
                .containsExactlyElementsOf(catDocumentModels.get(1).getFavoriteFoods());
        List<FavoriteToyModel> toys = model.getFavoriteToys();
        Assertions.assertThat(toys)
                .hasSize(catDocumentModels.get(1).getFavoriteToys().size());
        Assertions.assertThat(toys)
                .usingElementComparator(Comparator.comparing(FavoriteToyModel::getFavoriteToy))
                .containsExactlyElementsOf(catDocumentModels.get(1).getFavoriteToys());
    }
}
