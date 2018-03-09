
    create table cl_collaborator (
       role varchar(255) not null,
        space_id varchar(36) not null,
        user_id varchar(36) not null,
        primary key (space_id, user_id)
    );

    create table cl_document (
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

    create table cl_document_version (
       created_at timestamp not null,
        is_current_version integer not null,
        updated_at timestamp not null,
        importedDocument_id varchar(36) not null,
        document_id varchar(36) not null,
        primary key (importedDocument_id)
    );

    create table cl_file (
       id varchar(36) not null,
        checksum bigint not null,
        file blob,
        filename varchar(255) not null,
        primary key (id)
    );

    create table cl_imported_document (
       id varchar(36) not null,
        created_at timestamp not null,
        provider varchar(255) not null,
        status varchar(255) not null,
        updated_at timestamp not null,
        file_id varchar(36) not null,
        primary key (id)
    );

    create table cl_party (
       id varchar(36) not null,
        assignedId varchar(255) not null,
        created_at timestamp not null,
        name varchar(255) not null,
        updated_at timestamp not null,
        version integer,
        primary key (id)
    );

    create table cl_request (
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

    create table cl_space (
       id varchar(36) not null,
        assigned_id varchar(255) not null,
        created_at timestamp not null,
        description varchar(255),
        name varchar(255) not null,
        updated_at timestamp not null,
        version integer,
        primary key (id)
    );

    create table cl_user (
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

    alter table cl_document
       add constraint UK1g8cna9wmjf7rvym5de8otj5j unique (type, assigned_id, supplier_assigned_id);

    alter table cl_party
       add constraint UKlbhowa4pjy8y18dto6qn6sw0h unique (assignedId);

    alter table cl_space
       add constraint UKsdrlysemmnh3bs7q5rky8bbq2 unique (assigned_id);

    alter table cl_user
       add constraint UKo2u1kvk0249ki7q84ggorljc6 unique (username);

    alter table cl_user
       add constraint UKj02cpo2pptija0ujdlickuyoa unique (identity_id);

    alter table cl_collaborator
       add constraint FKrp82c2i571lmhouy8orvurueb
       foreign key (space_id)
       references cl_space;

    alter table cl_collaborator
       add constraint FKa1et8kcht137s1ugxy5o29bwa
       foreign key (user_id)
       references cl_user;

    alter table cl_document_version
       add constraint FKsdhniwfe4777yxgjeqeu1f9t3
       foreign key (document_id)
       references cl_document;

    alter table cl_document_version
       add constraint FKv8l5ddy51iailp4sqdum4ab6
       foreign key (importedDocument_id)
       references cl_imported_document;

    alter table cl_imported_document
       add constraint FKradvm1a0nxiw43dnhj1lq4kho
       foreign key (file_id)
       references cl_file;

    alter table cl_request
       add constraint FKj13h6esgslepp53yqggtc47ve
       foreign key (space_id)
       references cl_space;

    alter table cl_request
       add constraint FK895blhlaeni7aui0msi384qa8
       foreign key (user_id)
       references cl_user;

    alter table favorite_spaces
       add constraint FK8r5h03fqx6pli9928pdfuxgiw
       foreign key (user_id)
       references cl_user;

    alter table party_names
       add constraint FKexpsenj4kq537hkji9csf7pkh
       foreign key (party_id)
       references cl_party;

    alter table party_space_ids
       add constraint FK90u3crgakw86yv1fr3byr9o4s
       foreign key (party_id)
       references cl_party;

    alter table user_checks
       add constraint FKrmuxciolt09o5lrpcs749ie6v
       foreign key (document_id)
       references cl_document;

    alter table user_starts
       add constraint FKo1uskfejr4nvf5o7vkeuj1k3t
       foreign key (document_id)
       references cl_document;

    alter table user_views
       add constraint FKi71fggatfbea87cnlinjkns7s
       foreign key (document_id)
       references cl_document;
