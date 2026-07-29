package cv.zeemsv.api.application.solicitacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class EmailTemplateLinkHelperTest {
    private static final String LINK = "http://localhost:8082/viewer?path=abc&type=application%2Fpdf";
    private static final String EXPECTED_LINK = "<a href=\"http://localhost:8082/viewer?path=abc&amp;type=application%2Fpdf\">Recibo Pedido</a>";

    @Test
    void replacesStandaloneLinkPlaceholderWithClickableReciboPedidoText() {
        String body = "Para aceder ao recibo clique no link $LINK$.";

        String result = EmailTemplateLinkHelper.applyReciboPedidoLink(body, LINK);

        assertEquals("Para aceder ao recibo clique no link " + EXPECTED_LINK + ".", result);
    }

    @Test
    void keepsExistingAnchorTextAndFillsHrefPlaceholder() {
        String body = "Clique em <a href=\"$LINK$\">Recibo Pedido</a>.";

        String result = EmailTemplateLinkHelper.applyReciboPedidoLink(body, LINK);

        assertEquals("Clique em " + EXPECTED_LINK + ".", result);
    }

    @Test
    void replacesHttpWrappedLinkPlaceholderInAnchorHref() {
        String body = "Clique em <a href=\"http://$LINK$\">Recibo Pedido</a>.";

        String result = EmailTemplateLinkHelper.applyReciboPedidoLink(body, LINK);

        assertEquals("Clique em " + EXPECTED_LINK + ".", result);
    }

    @Test
    void turnsPlainReciboPedidoTextIntoClickableLink() {
        String body = "Clique em Recibo Pedido para abrir o documento.";

        String result = EmailTemplateLinkHelper.applyReciboPedidoLink(body, LINK);

        assertEquals("Clique em " + EXPECTED_LINK + " para abrir o documento.", result);
    }

    @Test
    void removesReciboSentenceWhenLinkIsMissing() {
        String body = "<p>Pedido submetido.</p><p>Para aceder ao recibo do pedido clique no link Recibo Pedido.</p>";

        String result = EmailTemplateLinkHelper.applyReciboPedidoLink(body, "");

        assertTrue(result.contains("Pedido submetido."));
        assertFalse(result.contains("Recibo Pedido"));
    }

    @Test
    void doesNotLeaveHttpLinkPlaceholderWhenLinkIsMissing() {
        String body = "<p>Pedido submetido.</p><p>Clique em <a href=\"http://$LINK$\">Recibo Pedido</a>.</p>";

        String result = EmailTemplateLinkHelper.applyReciboPedidoLink(body, "");

        assertTrue(result.contains("Pedido submetido."));
        assertFalse(result.contains("$LINK$"));
        assertFalse(result.contains("http://$LINK$"));
        assertFalse(result.contains("Recibo Pedido"));
    }
}
