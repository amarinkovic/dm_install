package pro.documentum.util.queries;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class ReservedWords {

    private static final Set<String> RESERVED = new HashSet<>();

    static {
        try {
            fillKeywords();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private ReservedWords() {
        super();
    }

    public static boolean isReserved(final String attrName) {
        String ucAttrName = Objects.requireNonNull(attrName).toUpperCase();
        return RESERVED.contains(ucAttrName);
    }

    public static String makeProjection(final Iterator<String> attrs) {
        return makeProjection(null, attrs);
    }

    public static String makeProjection(final String tableAlias,
            final Iterator<String> attrs) {
        if (attrs == null || !attrs.hasNext()) {
            throw new IllegalArgumentException("empty attributes");
        }
        boolean hasAlias = StringUtils.isNotBlank(tableAlias);
        StringBuilder result = new StringBuilder(10 * 15);
        while (attrs.hasNext()) {
            if (hasAlias) {
                result.append(tableAlias).append(".");
            }
            result.append(makeEscaped(attrs.next()));
            if (attrs.hasNext()) {
                result.append(',');
            }
        }
        return result.toString();
    }

    public static String makeProjection(final Iterable<String> attrs) {
        return makeProjection(null, attrs);
    }

    public static String makeProjection(final String tableAlias,
            final Iterable<String> attrs) {
        return makeProjection(tableAlias, attrs.iterator());
    }

    public static String makeEscaped(final String attrName) {
        if (isReserved(attrName)) {
            return "\"" + attrName + "\"";
        } else {
            return attrName;
        }
    }

    private static void fillKeywords() throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = ReservedWords.class
                    .getResourceAsStream("ReservedWords.txt");
            Objects.requireNonNull(inputStream,
                    "Unable to find ReservedWords.txt");
            for (String line : IOUtils.readLines(inputStream)) {
                if (StringUtils.isBlank(line)) {
                    continue;
                }
                for (String word : line.split("\\s+")) {
                    RESERVED.add(StringUtils.trim(word));
                }
            }
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }

}
