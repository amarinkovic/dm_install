package pro.documentum.jdo.query.expression;

import com.documentum.fc.common.DfUtil;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
class DQLString extends DQLLiteral<String> {

    DQLString(final String value) {
        super("'" + DfUtil.escapeQuotedString(value) + "'");
    }

}
