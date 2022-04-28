package io.github.jeyjeyemem.externalizedproperties.resolvers.database.queryexecutors;

import io.github.jeyjeyemem.externalizedproperties.resolvers.database.ConnectionProvider;
import io.github.jeyjeyemem.externalizedproperties.resolvers.database.testentities.H2Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SimpleNameValueQueryExecutorTests {
    static final int NUMBER_OF_TEST_ENTRIES = 2;
    static final String H2_CONNECTION_STRING = 
        H2Utils.buildConnectionString(SimpleNameValueQueryExecutorTests.class.getSimpleName());
    static final ConnectionProvider CONNECTION_PROVIDER = 
        H2Utils.createConnectionProvider(
            H2_CONNECTION_STRING, 
            "sa"
        );

    @BeforeAll
    static void setup() throws SQLException {
        createTestDatabaseConfigurationEntries();
    }

    @Nested
    class Constructor {
        @Test
        @DisplayName("should throw when schema argument is null")
        void test1() {
            assertThrows(
                IllegalArgumentException.class,
                () ->new SimpleNameValueQueryExecutor(
                    null, 
                    "my_table", 
                    "my_property_name", 
                    "my_property_value"
                )
            );
        }

        @Test
        @DisplayName("should throw when table argument is null or empty")
        void test2() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleNameValueQueryExecutor(
                    "schema", 
                    null, 
                    "my_property_name", 
                    "my_property_value"
                )
            );

            assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleNameValueQueryExecutor(
                    "schema", 
                    "", // Empty 
                    "my_property_name", 
                    "my_property_value"
                )
            );
        }

        @Test
        @DisplayName("should throw when property name column argument is null or empty")
        void test3() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleNameValueQueryExecutor(
                    "schema", 
                    "my_table", 
                    null, 
                    "my_property_value"
                )
            );

            assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleNameValueQueryExecutor(
                    "schema", 
                    "my_table", 
                    "", // Empty 
                    "my_property_value"
                )
            );
        }

        @Test
        @DisplayName("should throw when property value column argument is null or empty")
        void test4() {
            assertThrows(
                IllegalArgumentException.class,
                () -> new SimpleNameValueQueryExecutor(
                    "schema", 
                    "my_table", 
                    "my_property_name", 
                    null
                )
            );

            assertThrows(
                IllegalArgumentException.class,
                () ->new SimpleNameValueQueryExecutor(
                    "schema", 
                    "my_table", 
                    "my_property_na,me", 
                    "" // Empty 
                )
            );
        }
    }

    @Nested
    class QueryPropertiesMethod {

        @Test
        @DisplayName("should query requested properties")
        void test1() throws SQLException {
            SimpleNameValueQueryExecutor queryExecutor =
                new SimpleNameValueQueryExecutor();

            List<String> propertiesToQuery = Arrays.asList(
                "test.property.1",
                "test.property.2"
            );
            
            Map<String, String> resolved = queryExecutor.queryProperties(
                CONNECTION_PROVIDER.getConnection(),
                propertiesToQuery
            );

            assertEquals(propertiesToQuery.size(), resolved.size());
            
            assertEquals(
                "test/property/value/1", 
                resolved.get("test.property.1")
            );

            assertEquals(
                "test/property/value/2", 
                resolved.get("test.property.2")
            );
        }

        @Test
        @DisplayName("should query requested properties from the specified schema")
        void test2() throws SQLException {
            SimpleNameValueQueryExecutor queryExecutor =
                new SimpleNameValueQueryExecutor(
                    "PUBLIC", // PUBLIC is H2's default schema
                    SimpleNameValueQueryExecutor.TABLE,
                    SimpleNameValueQueryExecutor.PROPERTY_NAME_COLUMN,
                    SimpleNameValueQueryExecutor.PROPERTY_VALUE_COLUMN
                );

            List<String> propertiesToQuery = Arrays.asList(
                "test.property.1",
                "test.property.2"
            );
            
            Map<String, String> resolved = queryExecutor.queryProperties(
                CONNECTION_PROVIDER.getConnection(),
                propertiesToQuery
            );

            assertEquals(propertiesToQuery.size(), resolved.size());
            
            assertEquals(
                "test/property/value/1", 
                resolved.get("test.property.1")
            );

            assertEquals(
                "test/property/value/2", 
                resolved.get("test.property.2")
            );
        }

        @Test
        @DisplayName("should throw when schema is invalid")
        void test3() {
            SimpleNameValueQueryExecutor queryExecutor =
                new SimpleNameValueQueryExecutor(
                    "NON_EXISTENT_SCHEMA",
                    SimpleNameValueQueryExecutor.TABLE,
                    SimpleNameValueQueryExecutor.PROPERTY_NAME_COLUMN,
                    SimpleNameValueQueryExecutor.PROPERTY_VALUE_COLUMN
                );

            List<String> propertiesToQuery = Arrays.asList(
                "test.property.1",
                "test.property.2"
            );

            assertThrows(
                SQLException.class, 
                () -> queryExecutor.queryProperties(
                    CONNECTION_PROVIDER.getConnection(), 
                    propertiesToQuery
                )
            );
        }

        @Test
        @DisplayName("should throw when table is invalid")
        void test4() {
            SimpleNameValueQueryExecutor queryExecutor =
                new SimpleNameValueQueryExecutor(
                    "PUBLIC",
                    "NON_EXISTENT_TABLE",
                    SimpleNameValueQueryExecutor.PROPERTY_NAME_COLUMN,
                    SimpleNameValueQueryExecutor.PROPERTY_VALUE_COLUMN
                );

            List<String> propertiesToQuery = Arrays.asList(
                "test.property.1",
                "test.property.2"
            );

            assertThrows(
                SQLException.class, 
                () -> queryExecutor.queryProperties(
                    CONNECTION_PROVIDER.getConnection(), 
                    propertiesToQuery
                )
            );
        }

        @Test
        @DisplayName("should throw when property name column is invalid")
        void test12() {
            SimpleNameValueQueryExecutor queryExecutor =
                new SimpleNameValueQueryExecutor(
                    "PUBLIC",
                    SimpleNameValueQueryExecutor.TABLE,
                    "NON_EXISTENT_COLUMN",
                    SimpleNameValueQueryExecutor.PROPERTY_VALUE_COLUMN
                );

            List<String> propertiesToQuery = Arrays.asList(
                "test.property.1",
                "test.property.2"
            );

            assertThrows(
                SQLException.class, 
                () -> queryExecutor.queryProperties(
                    CONNECTION_PROVIDER.getConnection(), 
                    propertiesToQuery
                )
            );
        }

        @Test
        @DisplayName("should throw when property name column is invalid")
        void test13() {
            SimpleNameValueQueryExecutor queryExecutor =
                new SimpleNameValueQueryExecutor(
                    "PUBLIC",
                    SimpleNameValueQueryExecutor.TABLE,
                    SimpleNameValueQueryExecutor.PROPERTY_NAME_COLUMN,
                    "NON_EXISTENT_COLUMN"
                );

            List<String> propertiesToQuery = Arrays.asList(
                "test.property.1",
                "test.property.2"
            );

            assertThrows(
                SQLException.class, 
                () -> queryExecutor.queryProperties(
                    CONNECTION_PROVIDER.getConnection(), 
                    propertiesToQuery
                )
            );
        }
    }

    private static void createTestDatabaseConfigurationEntries() throws SQLException {
        try (Connection connection = CONNECTION_PROVIDER.getConnection()) {

            H2Utils.createPropertiesTable(
                connection, 
                NUMBER_OF_TEST_ENTRIES
            );

            connection.commit();
        }
    }
}
