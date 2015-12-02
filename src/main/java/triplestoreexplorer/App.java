package triplestoreexplorer;

import static spark.Spark.*;

import com.github.kevinsawicki.http.HttpRequest;
import triplestoreexplorer.controller.*;
import triplestoreexplorer.model.ViewModel;
import triplestoreexplorer.template.HandlebarsTemplateEngine;

/**
 * A Triplestore web application
 * @author Raymon de Looff, Thijs Clowting
 */
public class App {

    private String dataStore;

    public App() {
        this.dataStore = "http://localhost:3030/elvisimp/";
    }

    /**
     * Responsible for building the required routes
     */
    private void buildRoutes() {

        // Static resources location
        staticFileLocation("/public");

        // Define routes
        get("/", (req, res) -> "Hello World");

        // Data
        get("/data/:dataset/:page", (request, response) -> {
            ViewModel viewModel = new ViewModel();
            viewModel.addData("dataset", request.params(":dataset"));
            viewModel.addData("page", request.params(":page"));
            DataViewController dataViewController = new DataViewController(viewModel, "data");

            // Execute
            dataViewController.dispatch(request);

            return dataViewController.render();
        }, new HandlebarsTemplateEngine());

        // Datasets
        get("/datasets", (request, response) -> {
            ViewModel viewModel = new ViewModel();
            DatasetsViewController datasetsViewController = new DatasetsViewController(viewModel, "datasets");

            // Execute
            datasetsViewController.dispatch(request);

            return datasetsViewController.render();
        }, new HandlebarsTemplateEngine());

        get("/datasets/add", (request, response) -> {
            ViewModel viewModel = new ViewModel();
            DatasetsAddViewController datasetsAddViewController = new DatasetsAddViewController(viewModel, "add-datasets");

            // Execute
            datasetsAddViewController.dispatch(request);

            return datasetsAddViewController.render();
        }, new HandlebarsTemplateEngine());

        post("/datasets/add", (request, response) -> {
            int code = HttpRequest.post("http://localhost:3030/$/datasets").send(request.body()).code();
            response.redirect("/datasets");
            return null;
        });

        get("/datasets/remove/:dataset", (request, response) -> {
            HttpRequest deleteRequest = HttpRequest.delete("http://localhost:3030/$/datasets/" + request.params(":dataset"));
            response.status(deleteRequest.code());

            return deleteRequest.body();
        });

        // Query
        get("/query", (request, response) -> {
            ViewModel viewModel = new ViewModel();
            QueryViewController queryViewController = new QueryViewController(viewModel, "query");

            // Execute
            queryViewController.dispatch(request);

            return queryViewController.render();
        }, new HandlebarsTemplateEngine());

    }

    public static void main(String[] args) {
        App app = new App();

        // Build routes
        app.buildRoutes();
    }

}
