package pro.documentum.util.constants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.documentum.fc.common.IDfId;

/**
 * @author Andrey B. Panfilov <andrey@panfilov.tel>
 */
public final class DfConstants {

    private DfConstants() {
        super();
    }

    public static String getBaseType(final IDfId objectId) {
        return Tags.get(objectId.getTypePart());
    }

    public static final class Tags {

        private static final Map<Integer, String> TAGS;

        static {
            TAGS = new HashMap<>();
            TAGS.put(IDfId.DM_TYPE, Types.DM_TYPE);
            TAGS.put(IDfId.DM_CONTAINMENT, Types.DMR_CONTAINMENT);
            TAGS.put(IDfId.DM_CONTENT, Types.DMR_CONTENT);
            TAGS.put(IDfId.DM_SYSOBJECT, Types.DM_SYSOBJECT);
            TAGS.put(IDfId.DM_DOCUMENT, Types.DM_DOCUMENT);
            TAGS.put(IDfId.DM_FOLDER, Types.DM_FOLDER);
            TAGS.put(IDfId.DM_CABINET, Types.DM_CABINET);
            TAGS.put(IDfId.DM_ASSEMBLY, Types.DM_ASSEMBLY);
            TAGS.put(IDfId.DM_STORE, Types.DM_STORE);
            TAGS.put(IDfId.DM_METHOD, Types.DM_METHOD);
            TAGS.put(IDfId.DM_USER, Types.DM_USER);
            TAGS.put(IDfId.DM_GROUP, Types.DM_GROUP);
            TAGS.put(IDfId.DM_OUTPUTDEVICE, Types.DM_OUTPUTDEVICE);
            TAGS.put(IDfId.DM_ROUTER, Types.DM_ROUTER);
            TAGS.put(IDfId.DM_REGISTERED, Types.DM_REGISTERED);
            TAGS.put(IDfId.DM_QUEUE_ITEM, Types.DMI_QUEUE_ITEM);
            TAGS.put(IDfId.DM_VSTAMP, Types.DMI_VSTAMP);
            TAGS.put(IDfId.DM_INDEX, Types.DMI_INDEX);
            TAGS.put(IDfId.DM_SEQUENCE, Types.DMI_SEQUENCE);
            TAGS.put(IDfId.DM_TRANSACTION_LOG, Types.DMI_TRANSACTIONLOG);
            TAGS.put(IDfId.DM_OTHERFILE, Types.DMI_OTHERFILE);
            TAGS.put(IDfId.DM_REGISTRY, Types.DMI_REGISTRY);
            TAGS.put(IDfId.DM_FORMAT, Types.DM_FORMAT);
            TAGS.put(IDfId.DM_FILESTORE, Types.DM_FILESTORE);
            TAGS.put(IDfId.DM_LINKEDSTORE, Types.DM_LINKEDSTORE);
            TAGS.put(IDfId.DM_LINKRECORD, Types.DMI_LINKRECORD);
            TAGS.put(IDfId.DM_DISTRIBUTEDSTORE, Types.DM_DISTRIBUTEDSTORE);
            TAGS.put(IDfId.DM_REPLICA_RECORD, Types.DMI_REPLICA_RECORD);
            TAGS.put(IDfId.DM_TYPE_INFO, Types.DMI_TYPE_INFO);
            TAGS.put(IDfId.DM_DUMP_RECORD, Types.DM_DUMP_RECORD);
            TAGS.put(IDfId.DM_DUMP_OBJECT_RECORD, Types.DMI_DUMP_OBJECT_RECORD);
            TAGS.put(IDfId.DM_LOAD_RECORD, Types.DM_LOAD_RECORD);
            TAGS.put(IDfId.DM_LOAD_OBJECT_RECORD, Types.DMI_LOAD_OBJECT_RECORD);
            TAGS.put(IDfId.DM_CHANGE_RECORD, Types.DMI_CHANGE_RECORD);
            TAGS.put(IDfId.DM_STAGED_DOCUMENT, Types.DM_STAGED);
            TAGS.put(IDfId.DM_DIST_COMP_RECORD, Types.DMI_DIST_COMP_RECORD);
            TAGS.put(IDfId.DM_RELATION, Types.DM_RELATION);
            TAGS.put(IDfId.DM_RELATIONTYPE, Types.DM_RELATION_TYPE);
            TAGS.put(IDfId.DM_LOCATION, Types.DM_LOCATION);
            TAGS.put(IDfId.DM_FULLTEXT_INDEX, Types.DM_FULLTEXT_INDEX);
            TAGS.put(IDfId.DM_DOCBASE_CONFIG, Types.DM_DOCBASE_CONFIG);
            TAGS.put(IDfId.DM_SERVER_CONFIG, Types.DM_SERVER_CONFIG);
            TAGS.put(IDfId.DM_MOUNT_POINT, Types.DM_MOUNT_POINT);
            TAGS.put(IDfId.DM_BLOBSTORE, Types.DM_BLOBSTORE);
            TAGS.put(IDfId.DM_NOTE, Types.DM_NOTE);
            TAGS.put(IDfId.DM_ACL, Types.DM_ACL);
            TAGS.put(IDfId.DM_POLICY, Types.DM_POLICY);
            TAGS.put(IDfId.DM_REFERENCE, Types.DM_REFERENCE);
            TAGS.put(IDfId.DM_RECOVERY, Types.DMI_RECOVERY);
            TAGS.put(IDfId.DM_PACKAGE, Types.DMI_PACKAGE);
            TAGS.put(IDfId.DM_WORKITEM, Types.DMI_WORKITEM);
            TAGS.put(IDfId.DM_PROCESS, Types.DM_PROCESS);
            TAGS.put(IDfId.DM_ACTIVITY, Types.DM_ACTIVITY);
            TAGS.put(IDfId.DM_WORKFLOW, Types.DM_WORKFLOW);
            TAGS.put(IDfId.DM_DD_INFO, Types.DM_DD_INFO);
            TAGS.put(IDfId.DM_DOMAIN, Types.DM_DOMAIN);
            TAGS.put(IDfId.DM_AGGR_DOMAIN, Types.DM_AGGR_DOMAIN);
            TAGS.put(IDfId.DM_EXPRESSION, Types.DM_EXPRESSION);
            TAGS.put(IDfId.DM_LITERAL_EXPR, Types.DM_LITERAL_EXPR);
            TAGS.put(IDfId.DM_BUILTIN_EXPR, Types.DM_BUILTIN_EXPR);
            TAGS.put(IDfId.DM_FUNC_EXPR, Types.DM_FUNC_EXPR);
            TAGS.put(IDfId.DM_COND_EXPR, Types.DM_COND_EXPR);
            TAGS.put(IDfId.DM_COND_EXPR, Types.DM_COND_ID_EXPR);
            TAGS.put(IDfId.DM_COND_EXPR, Types.DMI_EXPR_CODE);
            TAGS.put(IDfId.DM_KEY, Types.DM_KEY);
            TAGS.put(IDfId.DM_VALUE_ASSIST, Types.DM_VALUE_ASSIST);
            TAGS.put(IDfId.DM_VALUE_LIST, Types.DM_VALUE_LIST);
            TAGS.put(IDfId.DM_VALUE_QUERY, Types.DM_VALUE_QUERY);
            TAGS.put(IDfId.DM_VALUE_FUNC, Types.DM_VALUE_FUNC);
            TAGS.put(IDfId.DM_FEDERATION, Types.DM_FEDERATION);
            TAGS.put(IDfId.DM_AUDIT_TRAIL, Types.DM_AUDITTRAIL);
            TAGS.put(IDfId.DM_EXTERNALSTORE_TAG, Types.DM_EXTERN_STORE);
            TAGS.put(IDfId.DM_EXTERNALSTORE_FILE_TAG, Types.DM_EXTERN_FILE);
            TAGS.put(IDfId.DM_EXTERNALSTORE_URL_TAG, Types.DM_EXTERN_URL);
            TAGS.put(IDfId.DM_EXTERNALSTORE_FREE_TAG, Types.DM_EXTERN_FREE);
            TAGS.put(IDfId.DM_SUBCONTENT, Types.DMI_SUBCONTENT);
            TAGS.put(IDfId.DM_FOREIGN_KEY, Types.DM_FOREIGN_KEY);
            TAGS.put(IDfId.DM_ALIAS_SET, Types.DM_ALIAS_SET);
            TAGS.put(IDfId.DM_PLUGIN, Types.DM_PLUGIN);
            TAGS.put(IDfId.DM_DD_COMMON_INFO, Types.DMI_DD_COMMON_INFO);
            TAGS.put(IDfId.DM_DD_TYPE_INFO, Types.DMI_DD_TYPE_INFO);
            TAGS.put(IDfId.DM_DD_ATTR_INFO, Types.DMI_DD_ATTR_INFO);
            TAGS.put(IDfId.DM_DISPLAY_CONFIG, Types.DM_DISPLAY_CONFIG);
            TAGS.put(IDfId.DM_SCOPE_CONFIG, Types.DM_SCOPE_CONFIG);
            TAGS.put(IDfId.DM_CA_STORE_TAG, Types.DM_CA_STORE);
            TAGS.put(IDfId.DM_PARTITION_SCHEME, Types.DM_PARTITION_SCHEME);
            TAGS.put(IDfId.DM_CLIENTRIGHTSDOMAIN_TAG,
                    Types.DM_CLIENT_RIGHTS_DOMAIN);
        }

        private Tags() {
            super();
        }

        public static String get(final int typePart) {
            return TAGS.get(typePart);
        }

    }

    public static final class Types {

        public static final String DM_TYPE = "dm_type";

        public static final String DMR_CONTAINMENT = "dmr_containment";

        public static final String DMR_CONTENT = "dmr_content";

        public static final String DM_SYSOBJECT = "dm_sysobject";

        public static final String DM_DOCUMENT = "dm_document";

        public static final String DM_FOLDER = "dm_folder";

        public static final String DM_CABINET = "dm_cabinet";

        public static final String DM_ASSEMBLY = "dm_assembly";

        public static final String DM_STORE = "dm_store";

        public static final String DM_METHOD = "dm_method";

        public static final String DM_USER = "dm_user";

        public static final String DM_GROUP = "dm_group";

        public static final String DM_OUTPUTDEVICE = "dm_outputdevice";

        public static final String DM_ROUTER = "dm_router";

        public static final String DM_REGISTERED = "dm_registered";

        public static final String DMI_QUEUE_ITEM = "dmi_queue_item";

        public static final String DMI_VSTAMP = "dmi_vstamp";

        public static final String DMI_INDEX = "dmi_index";

        public static final String DMI_SEQUENCE = "dmi_sequence";

        public static final String DMI_TRANSACTIONLOG = "dmi_transactionlog";

        public static final String DMI_OTHERFILE = "dmi_otherfile";

        public static final String DMI_REGISTRY = "dmi_registry";

        public static final String DM_FORMAT = "dm_format";

        public static final String DM_FILESTORE = "dm_filestore";

        public static final String DM_LINKEDSTORE = "dm_linkedstore";

        public static final String DMI_LINKRECORD = "dmi_linkrecord";

        public static final String DM_DISTRIBUTEDSTORE = "dm_distributedstore";

        public static final String DMI_REPLICA_RECORD = "dmi_replica_record";

        public static final String DMI_TYPE_INFO = "dmi_type_info";

        public static final String DM_DUMP_RECORD = "dm_dump_record";

        public static final String DMI_DUMP_OBJECT_RECORD = "dmi_dump_object_record";

        public static final String DM_LOAD_RECORD = "dm_load_record";

        public static final String DMI_LOAD_OBJECT_RECORD = "dmi_load_object_record";

        public static final String DMI_CHANGE_RECORD = "dmi_change_record";

        public static final String DM_STAGED = "dm_staged";

        public static final String DMI_DIST_COMP_RECORD = "dmi_dist_comp_record";

        public static final String DM_RELATION = "dm_relation";

        public static final String DM_RELATION_TYPE = "dm_relation_type";

        public static final String DM_LOCATION = "dm_location";

        public static final String DM_FULLTEXT_INDEX = "dm_fulltext_index";

        public static final String DM_DOCBASE_CONFIG = "dm_docbase_config";

        public static final String DM_SERVER_CONFIG = "dm_server_config";

        public static final String DM_MOUNT_POINT = "dm_mount_point";

        public static final String DM_BLOBSTORE = "dm_blobstore";

        public static final String DM_NOTE = "dm_note";

        public static final String DM_ACL = "dm_acl";

        public static final String DM_POLICY = "dm_policy";

        public static final String DM_REFERENCE = "dm_reference";

        public static final String DMI_RECOVERY = "dmi_recovery";

        public static final String DMI_PACKAGE = "dmi_package";

        public static final String DMI_WORKITEM = "dmi_workitem";

        public static final String DM_PROCESS = "dm_process";

        public static final String DM_ACTIVITY = "dm_activity";

        public static final String DM_WORKFLOW = "dm_workflow";

        public static final String DM_DD_INFO = "dm_dd_info";

        public static final String DM_DOMAIN = "dm_domain";

        public static final String DM_AGGR_DOMAIN = "dm_aggr_domain";

        public static final String DM_EXPRESSION = "dm_expression";

        public static final String DM_LITERAL_EXPR = "dm_literal_expr";

        public static final String DM_BUILTIN_EXPR = "dm_builtin_expr";

        public static final String DM_FUNC_EXPR = "dm_func_expr";

        public static final String DM_COND_EXPR = "dm_cond_expr";

        public static final String DM_COND_ID_EXPR = "dm_cond_id_expr";

        public static final String DMI_EXPR_CODE = "dmi_expr_code";

        public static final String DM_KEY = "dm_key";

        public static final String DM_VALUE_ASSIST = "dm_value_assist";

        public static final String DM_VALUE_LIST = "dm_value_list";

        public static final String DM_VALUE_QUERY = "dm_value_query";

        public static final String DM_VALUE_FUNC = "dm_value_func";

        public static final String DM_FEDERATION = "dm_federation";

        public static final String DM_AUDITTRAIL = "dm_audittrail";

        public static final String DM_EXTERN_STORE = "dm_extern_store";

        public static final String DM_EXTERN_FILE = "dm_extern_file";

        public static final String DM_EXTERN_URL = "dm_extern_url";

        public static final String DM_EXTERN_FREE = "dm_extern_free";

        public static final String DMI_SUBCONTENT = "dmi_subcontent";

        public static final String DM_FOREIGN_KEY = "dm_foreign_key";

        public static final String DM_ALIAS_SET = "dm_alias_set";

        public static final String DM_PLUGIN = "dm_plugin";

        public static final String DMI_DD_COMMON_INFO = "dmi_dd_common_info";

        public static final String DMI_DD_TYPE_INFO = "dmi_dd_type_info";

        public static final String DMI_DD_ATTR_INFO = "dmi_dd_attr_info";

        public static final String DM_DISPLAY_CONFIG = "dm_display_config";

        public static final String DM_SCOPE_CONFIG = "dm_scope_config";

        public static final String DM_CA_STORE = "dm_ca_store";

        public static final String DM_PARTITION_SCHEME = "dm_partition_scheme";

        public static final String DM_CLIENT_RIGHTS_DOMAIN = "dm_client_rights_domain";

        private static final Set<String> NONSUBTYPABLE;

        static {
            NONSUBTYPABLE = new HashSet<>();
            NONSUBTYPABLE.add(DMI_SUBCONTENT);
            NONSUBTYPABLE.add(DM_DUMP_RECORD);
            NONSUBTYPABLE.add(DMI_DUMP_OBJECT_RECORD);
            NONSUBTYPABLE.add(DM_LOAD_RECORD);
            NONSUBTYPABLE.add(DMI_LOAD_OBJECT_RECORD);
            NONSUBTYPABLE.add(DMI_QUEUE_ITEM);
            NONSUBTYPABLE.add(DMI_PACKAGE);
            NONSUBTYPABLE.add(DMI_TRANSACTIONLOG);
            NONSUBTYPABLE.add(DMI_RECOVERY);
            NONSUBTYPABLE.add(DM_ASSEMBLY);
            NONSUBTYPABLE.add(DMI_DD_TYPE_INFO);
            NONSUBTYPABLE.add(DMI_REGISTRY);
            NONSUBTYPABLE.add(DM_ACL);
            NONSUBTYPABLE.add(DMR_CONTAINMENT);
            NONSUBTYPABLE.add(DMI_DD_ATTR_INFO);
            NONSUBTYPABLE.add(DMI_REPLICA_RECORD);
            NONSUBTYPABLE.add(DMI_DIST_COMP_RECORD);
            NONSUBTYPABLE.add(DMR_CONTENT);
            NONSUBTYPABLE.add(DM_REFERENCE);
            NONSUBTYPABLE.add(DMI_WORKITEM);
            NONSUBTYPABLE.add(DM_PARTITION_SCHEME);
            NONSUBTYPABLE.add(DM_GROUP);
        }

        private Types() {
            super();
        }

        public static boolean isSubTypable(final String typeName) {
            return !NONSUBTYPABLE.contains(typeName);
        }

    }

}
