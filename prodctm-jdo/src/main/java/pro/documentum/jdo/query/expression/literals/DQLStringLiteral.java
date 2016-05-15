package pro.documentum.jdo.query.expression.literals;

import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public class DQLStringLiteral extends DQLLiteral<String> {

    DQLStringLiteral(final String value) {
        super(value, "'" + DfUtil.escapeQuotedString(value) + "'");
    }

}
