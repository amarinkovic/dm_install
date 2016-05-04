package pro.documentum.util.queries;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.documentum.fc.common.DfDocbaseConstants;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class ReservedWordsTest {

    @Test
    public void testReservedWords() throws Exception {
        for (String word : new String[] {"abort", "alter", "assemblies", "acl",
            "and", "assembly", "add", "any", "assistance", "add_ftindex",
            "append", "attr", "address", "application", "auto", "all", "as",
            "avg", "allow", "asc", "bag", "bool", "business", "begin",
            "boolean", "by", "between", "browse", "cabinet", "comment",
            "contains", "caching", "commit", "content_format", "change",
            "complete", "content_id", "character", "components", "count",
            "characters", "composite", "create", "char", "computed", "current",
            "check", "contain_id", "date", "deleted", "distinct", "dateadd",
            "dependency", "dm_session_dd_locale", "datediff", "depth",
            "docbasic", "datefloor", "desc", "document", "datetostring",
            "descend", "double", "day", "disable", "drop", "default",
            "disallow", "drop_ftindex", "delete", "display", "else", "enforce",
            "exec", "elseif", "escape", "execute", "enable", "estimate",
            "exists", "false", "for", "ft_optimizer", "first", "foreign",
            "fulltext", "float", "from", "function", "folder", "ftindex",
            "grant", "group", "having", "hits", "id", "int", "is", "if",
            "integer", "iscurrent", "in", "internal", "ispublic", "insert",
            "into", "isreplica", "join", "key", "language", "lightweight",
            "lite", "last", "like", "lower", "latest", "link", "left", "list",
            "mapping", "members", "month", "materialize", "mfile_url", "move",
            "materialization", "mhits", "mscore", "max", "min", "mcontentid",
            "modify", "node", "note", "nullid", "nodesort", "now", "nullint",
            "none", "null", "nullstring", "not", "nulldate", "of", "on",
            "order", "object", "only", "outer", "objects", "or", "owner",
            "page_no", "permit", "private", "parent", "policy", "privileges",
            "partition", "position", "property", "path", "primary", "public",
            "qry", "qualifiable", "rdbms", "relate", "report", "read",
            "remove", "request", "references", "repeating", "revoke",
            "register", "replaceif", "score", "smallint", "supertype",
            "search", "some", "superuser", "select", "state", "support",
            "separator", "storage", "synonym", "server", "string", "sysadmin",
            "set", "substr", "sysobj_id", "setfile", "substring", "system",
            "shareable", "sum", "shares", "summary", "table", "to",
            "transaction", "tag", "today", "true", "text", "tomorrow",
            "truncate", "time", "topic", "type", "tinyint", "tran", "union",
            "unregister", "update", "unique", "user", "upper", "unlink",
            "using", "value", "version", "violation", "values", "verity",
            "where", "without", "week", "with", "world", "within", "write",
            "year", "yesterday", }) {
            assertThat(word, ReservedWordMatcher.isReserved());
        }
    }

    @Test
    public void testNotReservedWords() throws Exception {
        for (Field field : DfDocbaseConstants.class.getFields()) {
            String attrName = (String) field.get(null);
            if ("contain_id".equalsIgnoreCase(attrName)
                    || "CURRENT".equalsIgnoreCase(attrName)) {
                continue;
            }
            assertThat(attrName, not(ReservedWordMatcher.isReserved()));
        }
    }

    static class ReservedWordMatcher extends TypeSafeMatcher<String> {

        public ReservedWordMatcher() {
            super();
        }

        @Override
        public void describeTo(final Description description) {
            description.appendText("is reserved");
        }

        @Override
        public boolean matchesSafely(final String string) {
            return ReservedWords.isReserved(string);
        }

        public static Matcher isReserved() {
            return new ReservedWordMatcher();
        }

    }
}
