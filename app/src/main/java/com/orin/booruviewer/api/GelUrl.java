package com.orin.booruviewer.api;

import com.orin.booruviewer.entity.Tag;

import java.util.HashMap;
import java.util.Set;

/**
 * Autocomplete URL
 * https://gelbooru.com/index.php?page=autocomplete2&term=&type=tag_query
 */
public class GelUrl {
    private final String apiKey;
    private final String userId;
    private final String page;
    private final String s;
    private final String q;
    private final String limit;
    private final String pid;
    private final Set<Tag> tags;
    private final String cid;
    private final String id;
    private final String name;
    private final String names;
    private final String json;
    private final String term;
    private final String order;
    private final String orderby;
    private final String type;
    private final String SITE;

    private GelUrl(Builder builder) {
        this.apiKey = builder.apiKey;
        this.userId = builder.userId;
        this.page = builder.page;
        this.s = builder.s;
        this.q = builder.q;
        this.limit = builder.limit;
        this.pid = builder.pid;
        this.tags = builder.tags;
        this.cid = builder.cid;
        this.id = builder.id;
        this.name = builder.name;
        this.names = builder.names;
        this.json = builder.json;
        this.term = builder.term;
        this.order = builder.order;
        this.orderby = builder.orderby;
        this.SITE = "https://gelbooru.com/index.php?";
        this.type = builder.type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(this.SITE)
                .append("&api_key=")
                .append(this.apiKey)
                .append("&user_id=")
                .append(this.userId);

        if (page != null)
            sb.append("&page=").append(page);

        if (s != null)
            sb.append("&s=").append(s);

        if (q != null)
            sb.append("&q=").append(q);

        if (limit != null)
            sb.append("&limit").append(limit);

        if (tags != null) {
            sb.append("&tags=");

            for (Tag tag : tags)
                sb.append(tag.getName()).append("+");
        }

        if (pid != null)
            sb.append("&pid=").append(pid);

        if (cid != null)
            sb.append("&cid=").append(cid);

        if (id != null)
            sb.append("&id=").append(id);

        if (name != null)
            sb.append("&name=").append(name);

        if (names != null)
            sb.append("&names=").append(names);

        if (json != null)
            sb.append("&json=").append(json);

        if (term != null)
            sb.append("&term=").append(term);

        if (order != null)
            sb.append("&order=").append(order);

        if (orderby != null)
            sb.append("&orderby=").append(orderby);

        if (type != null)
            sb.append("&type=").append(type);

        return sb.toString();
    }

    public static class Builder {
        private final String apiKey;
        private final String userId;
        private String page;
        private String s;
        private String q;
        private String limit;
        private String pid;
        private Set<Tag> tags;
        private String cid;
        private String id;
        private String name;
        private String names;
        private String term;
        private String order;
        private String orderby;
        private String json;
        private String type;

        public Builder(HashMap<String, String> credentials) {
            if (credentials.get("apiKey") == null || credentials.get("userId") == null) {
                throw new IllegalArgumentException("API Key and User ID can not be null");
            }
            this.apiKey = credentials.get("apiKey");
            this.userId = credentials.get("userId");
        }

        public Builder page(String page) {
            this.page = page;
            return this;
        }

        public Builder s(String s) {
            this.s = s;
            return this;
        }

        public Builder q(String q) {
            this.q = q;
            return this;
        }

        public Builder limit(String limit) {
            this.limit = limit;
            return this;
        }

        public Builder pid(String pid) {
            this.pid = pid;
            return this;
        }

        public Builder tags(Set<Tag> tags) {
            this.tags = tags;
            return this;
        }

        public Builder cid(String cid) {
            this.cid = cid;
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder names(String names) {
            this.names = names;
            return this;
        }

        public Builder json(boolean json) {
            this.json = json ? "1" : "0";
            return this;
        }

        public Builder term(String term) {
            this.term = term;
            return this;
        }

        public Builder order(String order) {
            this.order = order;
            return this;
        }

        public Builder orderby(String orderby) {
            this.orderby = orderby;
            return this;
        }

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public GelUrl build() {
            return new GelUrl(this);
        }
    }
}
