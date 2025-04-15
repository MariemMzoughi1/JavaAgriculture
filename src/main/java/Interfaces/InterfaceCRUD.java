package Interfaces;

import java.sql.SQLException;
import java.util.List;

public interface InterfaceCRUD <T>{
    public void add(T t) throws SQLException;
    public void update(T t);
    public void delete(T t);
    public List<T> find();
}