
    create table cn_collaborator (
       role varchar(255) not null,
        space_id varchar(36) not null,
        user_id varchar(36) not null,
        primary key (space_id, user_id)
    );

    create table cn_document (
       id varchar(36) not null,
        amount double,
        assigned_id varchar(255) not null,
        created_at timestamp not null,
        currency varchar(255),
        customer_assigned_id varchar(255),
        customer_city varchar(255),
        customer_country varchar(255),
        customer_name varchar(255),
        customer_street_address varchar(255),
        issue_date timestamp,
        supplier_assigned_id varchar(255) not null,
        supplier_city varchar(255),
        supplier_country varchar(255),
        supplier_name varchar(255),
        supplier_street_address varchar(255),
        tax double,
        type varchar(255) not null,
        updated_at timestamp not null,
        primary key (id)
    );

    create table cn_document_version (
       created_at timestamp not null,
        is_current_version integer not null,
        updated_at timestamp not null,
        importedDocument_id varchar(36) not null,
        document_id varchar(36) not null,
        primary key (importedDocument_id)
    );

    create table cn_file (
       id varchar(36) not null,
        checksum bigint not null,
        file blob,
        filename varchar(255) not null,
        primary key (id)
    );

    create table cn_imported_document (
       id varchar(36) not null,
        created_at timestamp not null,
        provider varchar(255) not null,
        status varchar(255) not null,
        updated_at timestamp not null,
        file_id varchar(36) not null,
        primary key (id)
    );

    create table cn_party (
       id varchar(36) not null,
        assignedId varchar(255) not null,
        created_at timestamp not null,
        name varchar(255) not null,
        updated_at timestamp not null,
        version integer,
        primary key (id)
    );

    create table cn_request (
       id varchar(36) not null,
        created_at timestamp not null,
        message varchar(255) not null,
        permission varchar(255) not null,
        status varchar(255) not null,
        updated_at timestamp not null,
        space_id varchar(36) not null,
        user_id varchar(36) not null,
        primary key (id)
    );

    create table cn_space (
       id varchar(36) not null,
        assigned_id varchar(255) not null,
        created_at timestamp not null,
        description varchar(255),
        name varchar(255) not null,
        updated_at timestamp not null,
        version integer,
        primary key (id)
    );

    create table cn_user (
       id varchar(36) not null,
        bio varchar(255),
        company varchar(255),
        created_at timestamp not null,
        default_language varchar(255),
        email varchar(255),
        full_name varchar(255),
        identity_id varchar(255) not null,
        image_url varchar(255),
        provider_type varchar(255) not null,
        registration_complete char(255) not null,
        updated_at timestamp not null,
        url varchar(255),
        username varchar(255) not null,
        version integer,
        primary key (id)
    );

    create table favorite_spaces (
       user_id varchar(36) not null,
        value varchar(255)
    );

    create table party_names (
       party_id varchar(36) not null,
        value varchar(255)
    );

    create table party_space_ids (
       party_id varchar(36) not null,
        value varchar(255)
    );

    create table user_checks (
       document_id varchar(36) not null,
        value varchar(255)
    );

    create table user_starts (
       document_id varchar(36) not null,
        value varchar(255)
    );

    create table user_views (
       document_id varchar(36) not null,
        value varchar(255)
    );

    alter table cn_document
       add constraint UKawubnbop2ucyh7b4ty92y1u76 unique (type, assigned_id, supplier_assigned_id);

    alter table cn_party
       add constraint UK8ad1rtqs4v86jy8ccbuwt5t7i unique (assignedId);

    alter table cn_space
       add constraint UKgbhk5oavmtev0gw0t8ntybrpd unique (assigned_id);

    alter table cn_user
       add constraint UK5wtbbbk9glk087qucfky5sfu9 unique (username);

    alter table cn_user
       add constraint UKdo3rrt9o142kgb3m9nhkt8pp7 unique (identity_id);

    alter table cn_collaborator
       add constraint FKnr72rb1n095hpodgidcig96u6
       foreign key (space_id)
       references cn_space;

    alter table cn_collaborator
       add constraint FKouusi44ya3fn5wduy8wt0x6fl
       foreign key (user_id)
       references cn_user;

    alter table cn_document_version
       add constraint FK50roi3nln8ckvt08r3ab5p0m1
       foreign key (document_id)
       references cn_document;

    alter table cn_document_version
       add constraint FKft90haapk8yrwqymo7hj3mr5r
       foreign key (importedDocument_id)
       references cn_imported_document;

    alter table cn_imported_document
       add constraint FK6akkeg77t8min2f48696jblpq
       foreign key (file_id)
       references cn_file;

    alter table cn_request
       add constraint FKk05gvkwnou6ih6sebk28fjxjp
       foreign key (space_id)
       references cn_space;

    alter table cn_request
       add constraint FKgxkool5a6l2fy2e0c22i534s7
       foreign key (user_id)
       references cn_user;

    alter table favorite_spaces
       add constraint FKius5et5hipwo4eu1cbuc49erg
       foreign key (user_id)
       references cn_user;

    alter table party_names
       add constraint FKtedp0ok09g8v4astyc4kvdjlp
       foreign key (party_id)
       references cn_party;

    alter table party_space_ids
       add constraint FKtfda6q5athdvxev8irpc9rl2m
       foreign key (party_id)
       references cn_party;

    alter table user_checks
       add constraint FKej20a44rajppr0cw8mpty091u
       foreign key (document_id)
       references cn_document;

    alter table user_starts
       add constraint FKg6ysj70hpmddqe55u2rb47te
       foreign key (document_id)
       references cn_document;

    alter table user_views
       add constraint FKghymygvc6g2cp86kufvw9uv8n
       foreign key (document_id)
       references cn_document;
