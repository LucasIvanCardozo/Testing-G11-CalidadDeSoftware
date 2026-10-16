package persistencia;

import java.io.Serializable;

/**
 * Clase AsociadoDTO que representa un Data Transfer Object para la entidad Asociado.
 * Implementa Serializable para permitir la serialización del objeto.
 * Contiene atributos como nombre, apellido, dni, ciudad, calle, numero, telefono e id.
 */
public class AsociadoDTO implements Serializable {
    private String nombre;
    private String apellido;
    private String dni;
    private String ciudad;
    private String calle;
    private int numero;
    private String telefono;

    /** Constructor por defecto de la clase AsociadoDTO.
     * <b>post:</b> se crea una instancia de AsociadoDTO a inicializar.
     */
    public AsociadoDTO() {}

    /** Constructor parametrizado de la clase AsociadoDTO.
     * <b>post:</b> se crea una instancia de AsociadoDTO con los valores proporcionados.
     * @param nombre El nombre del asociado.
     * @param apellido El apellido del asociado.
     * @param dni El DNI del asociado.
     * @param ciudad La ciudad del asociado.
     * @param calle La calle del asociado.
     * @param numero El número de la calle del asociado.
     * @param telefono El teléfono del asociado.
     */
    public AsociadoDTO(String nombre, String apellido, String dni, String ciudad, String calle, int numero, String telefono) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.dni = dni;
        this.ciudad = ciudad;
        this.calle = calle;
        this.numero = numero;
        this.telefono = telefono;
    }

    /**
     * Métodos getters y setters para los atributos de la clase AsociadoDTO.
     */
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        validarTexto(nombre, "nombre");
        this.nombre = nombre;
    }
    public String getApellido() {
        return apellido;
    }
    public void setApellido(String apellido) {
        validarTexto(apellido, "apellido");
        this.apellido = apellido;
    }
    public String getDni() {
        return dni;
    }
    public void setDni(String dni) {
        validarTexto(dni, "dni");
        this.dni = dni;
    }
    public String getCiudad() {
        return ciudad;
    }
    public void setCiudad(String ciudad) {
        validarTexto(ciudad, "ciudad");
        this.ciudad = ciudad;
    }
    public String getCalle() {
        return calle;
    }
    public void setCalle(String calle) {
        validarTexto(calle, "calle");
        this.calle = calle;
    }
    public int getNumero() {
        return numero;
    }
    public void setNumero(int numero) {
        if (numero < 0) {
            throw new IllegalArgumentException("El numero no puede ser negativo");
        }
        this.numero = numero;
    }
    public String getTelefono() {
        return telefono;
    }
    public void setTelefono(String telefono) {
        validarTexto(telefono, "telefono");
        this.telefono = telefono;
    }

    private static void validarTexto(String valor, String campo) {
        if (valor == null || valor.isEmpty()) {
            throw new IllegalArgumentException("El campo " + campo + " no puede ser nulo ni vacio");
        }
    }

}