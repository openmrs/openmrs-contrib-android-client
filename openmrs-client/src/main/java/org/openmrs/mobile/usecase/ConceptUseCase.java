package org.openmrs.mobile.usecase;

import android.util.Log;

import org.openmrs.mobile.api.retrofit.ConceptRepository;
import org.openmrs.mobile.application.OpenMRSLogger;
import org.openmrs.mobile.models.Concept;
import org.openmrs.mobile.models.Link;
import org.openmrs.mobile.models.Results;
import org.openmrs.mobile.utilities.NetworkUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConceptUseCase {

    private final static int MAX_CONCEPTS_IN_ONE_QUERY = 50;

    private final OpenMRSLogger logger;
    private final ConceptRepository repository;

    public ConceptUseCase(ConceptRepository repository) {
        this.logger = new OpenMRSLogger();
        this.repository = repository;
    }

    public Results<Concept> getConcepts(String filter) {
        return getConcepts(null, filter, MAX_CONCEPTS_IN_ONE_QUERY, 0);
    }

    public Results<Concept> getConcepts(String representation, String filter,
                                        Integer limit, Integer startIndex) {
        // get plain concepts
        Results<Concept> results = new Results<>();
        if (NetworkUtils.isOnline()) {
            logger.d("Get concepts from server");
            results = getPlainConcepts(null, filter, limit, startIndex);
        } else {
            logger.d("Get concepts from cache");
            List<Concept> concepts = repository.getCachedConcepts(filter);
            results.setResults(concepts);
            results.setLinks(Collections.emptyList());
        }
        return results;
    }

    public void filterConcepts(final String filter) {
        repository.searchConcepts(filter).enqueue(new Callback<Results<Concept>>() {

            @Override
            public void onResponse(Call<Results<Concept>> call, Response<Results<Concept>> response) {
                logger.d("searchConcepts: onResponse");
                if (!response.isSuccessful()) {
                    logger.d("searchConcepts: response is not successful");
                    return;
                }

                List<Concept> concepts = response.body().getResults();
                debug(concepts);

//                for (Link link : response.body().getLinks()) {
//                    logger.d("Link in response: " + link.getRel());
//                    logger.d("Link url: " + link.getUri());
//                }
                // TODO return result or replace with observable
            }

            @Override
            public void onFailure(Call<Results<Concept>> call, Throwable t) {
                logger.e("searchConcepts: onFailure", t);
            }
        });
    }

    // PRIVATE METHODS

    private Results<Concept> getPlainConcepts(String representation, String filter,
                                              Integer limit, Integer startIndex) {
        Results<Concept> results = new Results<>();
        try {
            results = repository.getConcepts(representation, filter, limit, startIndex).execute().body();
            for (Link link : results.getLinks()) {
                Log.d("TAG", "Link: " + link.getRel());
                if ("next".equals(link.getRel())) {
                    Log.d("TAG", "Link: " + link.getRel());
                    Results<Concept> concepts = getPlainConcepts(null,
                            filter,
                            MAX_CONCEPTS_IN_ONE_QUERY,
                            startIndex + MAX_CONCEPTS_IN_ONE_QUERY);
                    results.getResults().addAll(concepts.getResults());
                    results.getLinks().addAll(concepts.getLinks());
                    break;
                }
            }
        } catch (IOException e) {
            Log.e("TAG", "Failed to obtain concepts", e);
        }
//                .enqueue(getConceptsCallback(startIndex, callback));
        return results;
    }

    private void debug(List<? extends Concept> concepts) {
        logger.d("searchConcepts: filtered results: " + concepts.size());
        for (Concept concept : concepts) {
            logger.d("concept: " + concept.getName() + " / " + concept.getDisplay());
            printLinks(concept.getLinks());
        }
    }

    private void printLinks(List<Link> links) {
        for (Link link : links) {
            logger.d("Link: " + link.getRel() + " / " + link.getRel());
        }
    }

    private Callback<Results<Concept>> getConceptsCallback(final Integer startIndex, DomainCallback callback) {
        return new Callback<Results<Concept>>() {
            @Override
            public void onResponse(Call<Results<Concept>> call, Response<Results<Concept>> response) {
                logger.d("getConcepts: onResponse");
                if (!response.isSuccessful()) {
                    logger.d("getConcepts: response is not successful");
                    return;
                }

                callback.onResult(response.body().getResults());
//                ConceptDAO conceptDAO = new ConceptDAO();
//                if (response.body() != null) {
//                    for (Concept concept : response.body().getResults()) {
//                        conceptDAO.saveOrUpdate(concept);
//                        downloadedConcepts++;
//                    }
//                }
//
//                List<Concept> concepts = response.body().getResults();
//                debug(concepts);

//                boolean isNextPage = false;
//                if (response.body() != null) {
//                    for (Link link : response.body().getLinks()) {
//                        if ("next".equals(link.getRel())) {
//                            isNextPage = true;
////                            downloadConcepts(startIndex + MAX_CONCEPTS_IN_ONE_QUERY);
//                            break;
//                        }
//                    }
//                }
            }

            @Override
            public void onFailure(Call<Results<Concept>> call, Throwable t) {
                logger.e("searchConcepts: onFailure", t);
                callback.onFailure(t);
            }
        };
    }

    public interface DomainCallback {

        void onResult(List<Concept> results);

        void onFailure(Throwable t);

    }
}
