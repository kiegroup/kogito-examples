/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.kogito.examples;

import static io.restassured.RestAssured.given;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

@QuarkusIntegrationTest
class TimeoutIT {

    @BeforeAll
    static void init() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    public static final String SWITCH_STATE_TIMEOUT_WORKFLOW_PATH = "switch_state_timeouts";
    public static final String CALLBACK_STATE_TIMEOUT_WORKFLOW_PATH = "callback_state_timeouts";
    public static final String EVENT_STATE_TIMEOUT_WORKFLOW_PATH = "event_state_timeouts";
    public static final String TIMEOUT_WORKFLOW_PATH = "workflow_timeouts";

    @Test
    void testSwitchStateWorkflow() {
        testWorkflow(SWITCH_STATE_TIMEOUT_WORKFLOW_PATH);
    }

    @Test
    void testCallbackStateWorkflow() {
        testWorkflow(CALLBACK_STATE_TIMEOUT_WORKFLOW_PATH);
    }

    @Test
    void testEventStateWorkflow() {
        testWorkflow(EVENT_STATE_TIMEOUT_WORKFLOW_PATH);
    }
    
    @Test
    void testWorkflow() {
        testWorkflow(TIMEOUT_WORKFLOW_PATH);
    }

    private void testWorkflow(String workflowPath) {
        //create the workflow instance
        String id = given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body("{}").when()
                .post(workflowPath)
                .then()
                .statusCode(201)
                .extract()
                .body().path("id");

        //check workflow instance was created and active
        given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .get(workflowPath + "/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id));

        //check the workflow instance was timed-out and completed
        await()
                .pollDelay(15, TimeUnit.SECONDS)
                .atMost(1, TimeUnit.MINUTES)
                .untilAsserted(() -> {
                    given()
                            .contentType(ContentType.JSON)
                            .accept(ContentType.JSON)
                            .get(workflowPath + "/{id}", id)
                            .then()
                            .statusCode(404);
                });
    }
}
