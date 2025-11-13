
import br.univali.cc.prog3.banco.dominio.Banco;
import br.univali.cc.prog3.banco.visao.BancoGUI;

public class Principal {
    public static void main(String[] args) {
        Banco bb = new Banco("Banco do Brasil", "Feito para você", 1);
        BancoGUI banco = new BancoGUI(bb);
        banco.menu();
    }
}
