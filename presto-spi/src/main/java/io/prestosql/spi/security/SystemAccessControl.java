/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.spi.security;

import io.prestosql.spi.connector.CatalogSchemaName;
import io.prestosql.spi.connector.CatalogSchemaTableName;
import io.prestosql.spi.connector.SchemaTableName;

import java.security.Principal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static io.prestosql.spi.security.AccessDeniedException.denyAddColumn;
import static io.prestosql.spi.security.AccessDeniedException.denyCatalogAccess;
import static io.prestosql.spi.security.AccessDeniedException.denyCreateSchema;
import static io.prestosql.spi.security.AccessDeniedException.denyCreateTable;
import static io.prestosql.spi.security.AccessDeniedException.denyCreateView;
import static io.prestosql.spi.security.AccessDeniedException.denyCreateViewWithSelect;
import static io.prestosql.spi.security.AccessDeniedException.denyDeleteTable;
import static io.prestosql.spi.security.AccessDeniedException.denyDropColumn;
import static io.prestosql.spi.security.AccessDeniedException.denyDropSchema;
import static io.prestosql.spi.security.AccessDeniedException.denyDropTable;
import static io.prestosql.spi.security.AccessDeniedException.denyDropView;
import static io.prestosql.spi.security.AccessDeniedException.denyGrantTablePrivilege;
import static io.prestosql.spi.security.AccessDeniedException.denyInsertTable;
import static io.prestosql.spi.security.AccessDeniedException.denyRenameColumn;
import static io.prestosql.spi.security.AccessDeniedException.denyRenameSchema;
import static io.prestosql.spi.security.AccessDeniedException.denyRenameTable;
import static io.prestosql.spi.security.AccessDeniedException.denyRevokeTablePrivilege;
import static io.prestosql.spi.security.AccessDeniedException.denySelectColumns;
import static io.prestosql.spi.security.AccessDeniedException.denySetCatalogSessionProperty;
import static io.prestosql.spi.security.AccessDeniedException.denyShowSchemas;
import static io.prestosql.spi.security.AccessDeniedException.denyShowTablesMetadata;

public interface SystemAccessControl
{
    /**
     * Check if the principal is allowed to be the specified user.
     *
     * @throws AccessDeniedException if not allowed
     */
    void checkCanSetUser(Optional<Principal> principal, String userName);

    /**
     * Check if identity is allowed to set the specified system property.
     *
     * @throws AccessDeniedException if not allowed
     */
    void checkCanSetSystemSessionProperty(Identity identity, String propertyName);

    /**
     * Check if identity is allowed to access the specified catalog
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanAccessCatalog(Identity identity, String catalogName)
    {
        denyCatalogAccess(catalogName);
    }

    /**
     * Filter the list of catalogs to those visible to the identity.
     */
    default Set<String> filterCatalogs(Identity identity, Set<String> catalogs)
    {
        return Collections.emptySet();
    }

    /**
     * Check if identity is allowed to create the specified schema in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanCreateSchema(Identity identity, CatalogSchemaName schema)
    {
        denyCreateSchema(schema.toString());
    }

    /**
     * Check if identity is allowed to drop the specified schema in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanDropSchema(Identity identity, CatalogSchemaName schema)
    {
        denyDropSchema(schema.toString());
    }

    /**
     * Check if identity is allowed to rename the specified schema in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanRenameSchema(Identity identity, CatalogSchemaName schema, String newSchemaName)
    {
        denyRenameSchema(schema.toString(), newSchemaName);
    }

    /**
     * Check if identity is allowed to execute SHOW SCHEMAS in a catalog.
     * <p>
     * NOTE: This method is only present to give users an error message when listing is not allowed.
     * The {@link #filterSchemas} method must filter all results for unauthorized users,
     * since there are multiple ways to list schemas.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanShowSchemas(Identity identity, String catalogName)
    {
        denyShowSchemas();
    }

    /**
     * Filter the list of schemas in a catalog to those visible to the identity.
     */
    default Set<String> filterSchemas(Identity identity, String catalogName, Set<String> schemaNames)
    {
        return Collections.emptySet();
    }

    /**
     * Check if identity is allowed to create the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanCreateTable(Identity identity, CatalogSchemaTableName table)
    {
        denyCreateTable(table.toString());
    }

    /**
     * Check if identity is allowed to drop the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanDropTable(Identity identity, CatalogSchemaTableName table)
    {
        denyDropTable(table.toString());
    }

    /**
     * Check if identity is allowed to rename the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanRenameTable(Identity identity, CatalogSchemaTableName table, CatalogSchemaTableName newTable)
    {
        denyRenameTable(table.toString(), newTable.toString());
    }

    /**
     * Check if identity is allowed to show metadata of tables by executing SHOW TABLES, SHOW GRANTS etc. in a catalog.
     * <p>
     * NOTE: This method is only present to give users an error message when listing is not allowed.
     * The {@link #filterTables} method must filter all results for unauthorized users,
     * since there are multiple ways to list tables.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanShowTablesMetadata(Identity identity, CatalogSchemaName schema)
    {
        denyShowTablesMetadata(schema.toString());
    }

    /**
     * Filter the list of tables and views to those visible to the identity.
     */
    default Set<SchemaTableName> filterTables(Identity identity, String catalogName, Set<SchemaTableName> tableNames)
    {
        return Collections.emptySet();
    }

    /**
     * Check if identity is allowed to add columns to the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanAddColumn(Identity identity, CatalogSchemaTableName table)
    {
        denyAddColumn(table.toString());
    }

    /**
     * Check if identity is allowed to drop columns from the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanDropColumn(Identity identity, CatalogSchemaTableName table)
    {
        denyDropColumn(table.toString());
    }

    /**
     * Check if identity is allowed to rename a column in the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanRenameColumn(Identity identity, CatalogSchemaTableName table)
    {
        denyRenameColumn(table.toString());
    }

    /**
     * Check if identity is allowed to select from the specified columns in a relation.  The column set can be empty.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanSelectFromColumns(Identity identity, CatalogSchemaTableName table, Set<String> columns)
    {
        denySelectColumns(table.toString(), columns);
    }

    /**
     * Check if identity is allowed to insert into the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanInsertIntoTable(Identity identity, CatalogSchemaTableName table)
    {
        denyInsertTable(table.toString());
    }

    /**
     * Check if identity is allowed to delete from the specified table in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanDeleteFromTable(Identity identity, CatalogSchemaTableName table)
    {
        denyDeleteTable(table.toString());
    }

    /**
     * Check if identity is allowed to create the specified view in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanCreateView(Identity identity, CatalogSchemaTableName view)
    {
        denyCreateView(view.toString());
    }

    /**
     * Check if identity is allowed to drop the specified view in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanDropView(Identity identity, CatalogSchemaTableName view)
    {
        denyDropView(view.toString());
    }

    /**
     * Check if identity is allowed to create a view that selects from the specified columns in a relation.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanCreateViewWithSelectFromColumns(Identity identity, CatalogSchemaTableName table, Set<String> columns)
    {
        denyCreateViewWithSelect(table.toString(), identity);
    }

    /**
     * Check if identity is allowed to set the specified property in a catalog.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanSetCatalogSessionProperty(Identity identity, String catalogName, String propertyName)
    {
        denySetCatalogSessionProperty(propertyName);
    }

    /**
     * Check if identity is allowed to grant the specified privilege to the grantee on the specified table.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanGrantTablePrivilege(Identity identity, Privilege privilege, CatalogSchemaTableName table, String grantee, boolean withGrantOption)
    {
        denyGrantTablePrivilege(privilege.toString(), table.toString());
    }

    /**
     * Check if identity is allowed to revoke the specified privilege on the specified table from the revokee.
     *
     * @throws AccessDeniedException if not allowed
     */
    default void checkCanRevokeTablePrivilege(Identity identity, Privilege privilege, CatalogSchemaTableName table, String revokee, boolean grantOptionFor)
    {
        denyRevokeTablePrivilege(privilege.toString(), table.toString());
    }
}
