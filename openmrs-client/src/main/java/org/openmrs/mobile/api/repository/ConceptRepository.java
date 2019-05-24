package org.openmrs.mobile.api.repository;

import org.openmrs.mobile.api.RestApi;
import org.openmrs.mobile.api.RestServiceBuilder;
import org.openmrs.mobile.dao.ConceptDAO;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.ApplicationConstants;

import java.util.List;

import retrofit2.Call;

public class ConceptRepository extends RetrofitRepository {

    private final RestApi api;
    private final ConceptDAO cache;

    public ConceptRepository() {
        this.api = RestServiceBuilder.createService(RestApi.class);
        this.cache = new ConceptDAO();
    }

    public Call<Results<Concept>> getConcepts(String representation, String searchQuery, Integer limit, Integer startIndex) {
        return api.getConcepts(representation, searchQuery, limit, startIndex);
    }

    public List<Concept> getCachedConcepts(String searchQuery) {
        return cache.findConceptsByName(searchQuery);
    }

    public Call<Results<Concept>> searchConcepts(String query) {
        return api.getConceptRefTerm(ApplicationConstants.API.FULL, query);
    }
}
