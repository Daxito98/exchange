package pl.slaska.it.exchange.dao;

import org.jasypt.util.password.BasicPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pl.slaska.it.exchange.model.Users;
import pl.slaska.it.exchange.model.Wallet;

import javax.sql.DataSource;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.List;


@Repository
public class WalletDAO {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    public void addUser(Users users) {
        jdbcTemplate.update("INSERT INTO Users VALUES(?,?,?,?,?,?,?,?)", users.getId(), users.getBalance(), users.getCredit_card(), users.getAge(), users.getEmail(), users.getName(),passwordEncryptor.encryptPassword(users.getPassword()), users.getPhone());
        jdbcTemplate.update("INSERT INTO Wallet VALUES(?,?,?)", getAddress(),0,users.getId());
    }

    public void updateWallet(float quantity, String wallet) {
        jdbcTemplate.update("UPDATE Wallet SET quantity =? WHERE idWallet =?",
                quantity,wallet);
    }

    public void deleteUser(String id) {
        jdbcTemplate.update("DELETE FROM Users WHERE id =?", id);
    }

    public Wallet getWalletByID(String id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Wallet WHERE idUser =?",
                    new WalletRowMapper(), id);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Wallet getWallet(String wallet) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM Wallet WHERE idWallet =?",
                    new WalletRowMapper(), wallet);
        }
        catch(EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Users> getUsers(){
        try{
            return jdbcTemplate.query(
                    "SELECT * FROM Users",
                    new UsersRowMapper());
        }
        catch(EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }

    public String getAddress(){
        String resultado = "0x";
        char tablaCesar[] = {
                'A', 'B', 'C', 'D', 'E',
                'F', 'G', 'H', 'I', 'J',
                'K', 'L', 'M', 'N', 'O',
                'P', 'Q', 'R', 'S', 'T',
                'U', 'V', 'W', 'X', 'Y',
                'Z',
                'a', 'b', 'c', 'd', 'e',
                'f', 'g', 'h', 'i', 'j',
                'k', 'l', 'm', 'n', 'o',
                'p', 'q', 'r', 's', 't',
                'u', 'v', 'w', 'x', 'y',
                'z', '1','2','3','4','5',
                '6','7','8','9','0'
        };
        for (int i = 0; i < 38; i++){
            int numero = (int)(Math.random()*tablaCesar.length);
            resultado+=tablaCesar[numero];
        }
        return resultado;
    }
}
