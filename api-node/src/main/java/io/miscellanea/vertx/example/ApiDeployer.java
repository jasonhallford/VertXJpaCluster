package io.miscellanea.vertx.example;

import io.vertx.config.ConfigRetriever;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.ext.cluster.infinispan.InfinispanClusterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class used to initialize the JPA runtime (Hibernate, in this case) and deploy the example's
 * verticles, of which there are two:
 *
 * <ol>
 *   <li><code>PeopleApiVertical</code>, which presents a simple RESTful API for creating, listing,
 *       and finding instances of <code>Person</code>
 *   <li><code>JpaRepositoryVerticle</code>, which executes as a worker verticle and handles the JPA
 *       interface with the database (H2, in this case)
 * </ol>
 *
 * This is example code, and as such is light on error handling, etc. It's primary purpose is to
 * demonstrate how one might integrate JPA with Vert.x to implement a database-backed API.
 */
public class ApiDeployer {
  // Fields
  private static final Logger LOGGER = LoggerFactory.getLogger(ApiDeployer.class);

  public static void main(String[] args) {
    // Bootstrap the infinispan cluster manager
    var clusterMgr = new InfinispanClusterManager();

    LOGGER.debug("Bootstrapping the Vert.x runtime in cluster mode..");
    var vertxOpts = new VertxOptions().setClusterManager(clusterMgr);
    Vertx.clusteredVertx(
        vertxOpts,
        result -> {
          if (result.succeeded()) {
            LOGGER.debug("Vert.x runtime initialized.");
            var vertx = result.result();

            var configRetrieverOpts =
                ConfigStoreHelper.buildDefaultRetrieverOptions("conf/api-config.json");

            // Deploy the application's verticles.
            ConfigRetriever.create(vertx, configRetrieverOpts)
                .getConfig(
                    config -> {
                      // Deploy the REST API verticle
                      var apiOpts = new DeploymentOptions().setConfig(config.result());
                      vertx.deployVerticle(ApiVerticle.class.getName(), apiOpts);
                    });

          } else {
            LOGGER.error("Unable to initialize Vert.x cluster node.", result.cause());
            result.result().close();
          }
        });
  }
}
