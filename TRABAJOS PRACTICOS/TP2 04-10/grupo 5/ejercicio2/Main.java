package tp2.ejercicio2;

public class Main {

    public static void main(String[] args) {

        // Ciudades
        Ciudad buenosAires = new Ciudad(
                "Buenos Aires",
                "Argentina"
        );

        Ciudad madrid = new Ciudad(
                "Madrid",
                "España"
        );

        // Aeropuertos
        Aeropuerto ezeiza = new Aeropuerto(
                "EZE",
                "Aeropuerto Internacional de Ezeiza",
                buenosAires
        );

        Aeropuerto barajas = new Aeropuerto(
                "MAD",
                "Aeropuerto Adolfo Suárez Madrid-Barajas",
                madrid
        );

        // Avión
        Avion avion = new Avion(
                "LV-ABC",
                "Boeing 787",
                250
        );

        // Operador
        OperadorComunicaciones operador =
                new OperadorComunicaciones(
                        "30000000",
                        "Carlos",
                        "Lopez"
                );

        // Tripulación
        Tripulacion tripulacion = new Tripulacion(operador);

        tripulacion.agregarPiloto(
                new Piloto("30000001", "Juan", "Perez")
        );

        tripulacion.agregarPiloto(
                new Piloto("30000002", "Pedro", "Gomez")
        );

        tripulacion.agregarComisario(
                new ComisarioAbordo(
                        "30000003",
                        "Martin",
                        "Diaz"
                )
        );

        tripulacion.agregarComisario(
                new ComisarioAbordo(
                        "30000004",
                        "Lucas",
                        "Fernandez"
                )
        );

        tripulacion.agregarAzafata(
                new Azafata("30000005", "Ana", "Garcia")
        );

        tripulacion.agregarAzafata(
                new Azafata("30000006", "Sofia", "Martinez")
        );

        tripulacion.agregarAzafata(
                new Azafata("30000007", "Laura", "Rodriguez")
        );

        tripulacion.agregarAzafata(
                new Azafata("30000008", "Maria", "Sanchez")
        );

        // Vuelo
        Vuelo vuelo = new Vuelo(
                "AR100",
                "10/10/2026",
                ezeiza,
                barajas,
                avion,
                tripulacion
        );

        // Pasajero y asiento
        Asiento asiento = new Asiento("15A");

        Pasajero pasajero = new Pasajero(
                "40000000",
                "Federico",
                "Perez",
                asiento
        );

        vuelo.agregarPasajero(pasajero);

        // Aerolínea
        Aerolinea aerolinea =
                new Aerolinea("Aerolínea Argentina");

        aerolinea.agregarVuelo(vuelo);

        // Prueba
        System.out.println(
                "Aerolinea: " + aerolinea.getNombre()
        );

        System.out.println(
                "Vuelo: " + vuelo.getNumero()
        );

        System.out.println(
                "Origen: " + vuelo.getOrigen().getNombre()
        );

        System.out.println(
                "Destino: " + vuelo.getDestino().getNombre()
        );

        System.out.println(
                "Pasajero: "
                        + pasajero.getNombre()
                        + " - Asiento: "
                        + pasajero.getAsiento().getNumero()
        );
    }
}