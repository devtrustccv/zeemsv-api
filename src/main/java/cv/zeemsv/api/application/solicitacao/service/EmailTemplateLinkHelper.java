package cv.zeemsv.api.application.solicitacao.service;

final class EmailTemplateLinkHelper {
    private static final String RECIBO_PEDIDO_SENTENCE_PATTERN =
        "(?is)\\s*(?:<p>)?Para\\s+aceder\\s+ao\\s+recibo\\s+do\\s+pedido\\s+clique\\s+no\\s+link\\s+Recibo\\s+Pedido\\.?\\s*(?:</p>)?";

    private EmailTemplateLinkHelper() {
    }

    static String applyReciboPedidoLink(String template, String linkRecibo) {
        if (!hasText(template)) {
            return template;
        }
        if (!hasText(linkRecibo)) {
            return removeReciboPedidoLink(template);
        }

        String value = template;
        String htmlLink = buildHtmlLink(linkRecibo, "Recibo Pedido");
        value = replaceUrlPlaceholders(value, linkRecibo, "LINK", "link", "linkRecibo", "LINK_RECIBO");
        value = replaceHrefPlaceholders(value, linkRecibo, "LINK", "link", "linkRecibo", "LINK_RECIBO", "link_recibo");
        value = replaceTemplateValue(value, htmlLink, "LINK", "linkReciboHtml", "link_recibo_html", "reciboHtml", "recibo_html", "LINK_RECIBO_HTML", "RECIBO_HTML");
        value = replaceTemplateValue(value, htmlLink, "linkRecibo", "link_recibo", "recibo", "link", "LINK_RECIBO", "RECIBO");
        value = replaceRawTemplateValue(value, htmlLink, "link_recibo_html", "LINK_RECIBO_HTML", "recibo_html", "RECIBO_HTML");
        value = replaceRawTemplateValue(value, htmlLink, "link_recibo", "LINK_RECIBO");
        return replaceReciboPedidoText(value, htmlLink);
    }

    private static String removeReciboPedidoLink(String template) {
        return template
            .replaceAll(RECIBO_PEDIDO_SENTENCE_PATTERN, "")
            .replaceAll("(?is)<p>[^<]*(?:\\$LINK\\$|\\$LINK_RECIBO\\$|link_recibo|LINK_RECIBO|Recibo Pedido).*?</p>", "")
            .replaceAll("(?is)<a\\b[^>]*(?:\\$LINK\\$|\\$LINK_RECIBO\\$|link_recibo|LINK_RECIBO)[^>]*>.*?</a>", "")
            .replace("http://$LINK$", "")
            .replace("https://$LINK$", "")
            .replace("$LINK$", "")
            .replace("$LINK_RECIBO$", "")
            .replace("link_recibo", "")
            .replace("LINK_RECIBO", "");
    }

    private static String replaceReciboPedidoText(String value, String htmlLink) {
        String valueWithFixedAnchors = value.replaceAll("(?is)<a\\b[^>]*>\\s*Recibo Pedido\\s*</a>", htmlLink);
        if (!valueWithFixedAnchors.equals(value) || valueWithFixedAnchors.contains(htmlLink)) {
            return valueWithFixedAnchors;
        }
        return value.replace("Recibo Pedido", htmlLink);
    }

    private static String replaceTemplateValue(String template, String replacement, String... keys) {
        String value = template;
        String safeReplacement = emptyIfNull(replacement);
        for (String key : keys) {
            value = value
                .replace("${" + key + "}", safeReplacement)
                .replace("{{" + key + "}}", safeReplacement)
                .replace("{" + key + "}", safeReplacement)
                .replace("#" + key + "#", safeReplacement)
                .replace(":" + key, safeReplacement)
                .replace("@" + key + "@", safeReplacement)
                .replace("$" + key + "$", safeReplacement)
                .replace("[[" + key + "]]", safeReplacement);
        }
        return value;
    }

    private static String replaceHrefPlaceholders(String template, String replacement, String... keys) {
        String value = template;
        String safeReplacement = escapeHtml(emptyIfNull(replacement));
        for (String key : keys) {
            value = value
                .replace("href=\"${" + key + "}\"", "href=\"" + safeReplacement + "\"")
                .replace("href='${" + key + "}'", "href='" + safeReplacement + "'")
                .replace("href=\"{{" + key + "}}\"", "href=\"" + safeReplacement + "\"")
                .replace("href='{{" + key + "}}'", "href='" + safeReplacement + "'")
                .replace("href=\"{" + key + "}\"", "href=\"" + safeReplacement + "\"")
                .replace("href='{" + key + "}'", "href='" + safeReplacement + "'")
                .replace("href=\"$" + key + "$\"", "href=\"" + safeReplacement + "\"")
                .replace("href='$" + key + "$'", "href='" + safeReplacement + "'");
        }
        return value;
    }

    private static String replaceUrlPlaceholders(String template, String replacement, String... keys) {
        String value = template;
        String safeReplacement = emptyIfNull(replacement);
        for (String key : keys) {
            value = value
                .replace("http://${" + key + "}", safeReplacement)
                .replace("https://${" + key + "}", safeReplacement)
                .replace("http://{" + key + "}", safeReplacement)
                .replace("https://{" + key + "}", safeReplacement)
                .replace("http://$" + key + "$", safeReplacement)
                .replace("https://$" + key + "$", safeReplacement)
                .replace("http://#" + key + "#", safeReplacement)
                .replace("https://#" + key + "#", safeReplacement);
        }
        return value;
    }

    private static String replaceRawTemplateValue(String template, String replacement, String... keys) {
        String value = template;
        String safeReplacement = emptyIfNull(replacement);
        for (String key : keys) {
            value = value.replace(key, safeReplacement);
        }
        return value;
    }

    private static String buildHtmlLink(String url, String label) {
        String safeUrl = escapeHtml(url);
        return "<a href=\"" + safeUrl + "\">" + escapeHtml(label) + "</a>";
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }
        return value
            .replace("&", "&amp;")
            .replace("\"", "&quot;")
            .replace("<", "&lt;")
            .replace(">", "&gt;");
    }

    private static String emptyIfNull(String value) {
        return value != null ? value : "";
    }

    private static boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
