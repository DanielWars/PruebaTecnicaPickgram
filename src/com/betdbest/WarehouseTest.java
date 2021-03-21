package com.betdbest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;


public class WarehouseTest {

  static final String SEMICOLON = ";";
  static final String COLON = ",";
  static final DateTimeFormatter DATE_PATTERN = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm");
  static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.000");

  static final Float EXPERIENCE_PRICE_BY_HOUR = 0.03f;

  static enum Warehouse {
    NEW_YORK(-4), SAN_FRANCISCO(-7);

    private Integer timeZoneOffset;

    Warehouse(int timeZoneOffset) {
      this.timeZoneOffset = timeZoneOffset;
    }

    static Warehouse fromName(String input) {
      switch (input) {
      case "New York":
        return NEW_YORK;
      case "San Francisco":
        return SAN_FRANCISCO;
      }
      return null;
    }

    public String toName() {
      switch (this) {
      case NEW_YORK:
        return "New York";
      case SAN_FRANCISCO:
        return "San Francisco";
      }
      return null;
    }

    public Integer getTimeZoneOffset() {
      return timeZoneOffset;
    }
  }

  static class Stock {
    final String itemId;
    final Warehouse warehouse;
    final int stock;

    Stock(String itemId, Warehouse warehouse, int stock) {
      this.itemId = itemId;
      this.warehouse = warehouse;
      this.stock = stock;
    }

    public String getItemId() {
      return itemId;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public int getStock() {
      return stock;
    }

  }

  static class BoxType {
    final String boxType;
    final int maxWeight;
    final int length, width, height; // cm
    final float volume; // dm3

    BoxType(String boxType, int maxWeight, int length, int width, int height, float volume) {
      this.boxType = boxType;
      this.maxWeight = maxWeight;
      this.length = length;
      this.width = width;
      this.height = height;
      this.volume = volume;
    }

    public String getBoxType() {
      return boxType;
    }

    public int getMaxWeight() {
      return maxWeight;
    }

    public int getLength() {
      return length;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

    public float getVolume() {
      return volume;
    }

  }

  static class CarrierPricing {
    final Warehouse warehouse;
    final String targetState;
    final float volumePrice; // $/dm3

    CarrierPricing(Warehouse warehouse, String targetState, float volumePrice) {
      this.warehouse = warehouse;
      this.targetState = targetState;
      this.volumePrice = volumePrice;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public String getTargetState() {
      return targetState;
    }

    public float getVolumePrice() {
      return volumePrice;
    }

  }

  static class ShippingHour {
    final DayOfWeek day;
    final LocalTime time;

    ShippingHour(DayOfWeek day, LocalTime time) {
      this.time = time;
      this.day = day;
    }

    public DayOfWeek getDay() {
      return day;
    }

    public LocalTime getTime() {
      return time;
    }

  }

  static class DepartureTime {
    final Warehouse warehouse;
    final String targetState;
    final List<ShippingHour> shippingHours;

    DepartureTime(Warehouse warehouse, String targetState, List<ShippingHour> shippingHours) {
      this.warehouse = warehouse;
      this.targetState = targetState;
      this.shippingHours = shippingHours;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public String getTargetState() {
      return targetState;
    }

    public List<ShippingHour> getShippingHours() {
      return shippingHours;
    }
  }

  static class CarrierTime {
    final Warehouse warehouse;
    final String targetState;
    final int carrierTime; // in hours

    CarrierTime(Warehouse warehouse, String targetState, int carrierTime) {
      this.warehouse = warehouse;
      this.targetState = targetState;
      this.carrierTime = carrierTime;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public String getTargetState() {
      return targetState;
    }

    public int getCarrierTime() {
      return carrierTime;
    }

  }

  static class Item {
    final String itemId;
    final int weight;
    final int length, width, height;

    Item(String itemId, int weight, int length, int width, int height) {
      this.itemId = itemId;
      this.weight = weight;
      this.length = length;
      this.width = width;
      this.height = height;
    }

    public String getItemId() {
      return itemId;
    }

    public int getLength() {
      return length;
    }

    public int getWeight() {
      return weight;
    }

    public int getWidth() {
      return width;
    }

    public int getHeight() {
      return height;
    }

  }

  static class Order {
    final long orderId;
    final LocalDateTime orderDate;
    final String itemId;
    final String targetState;

    Order(long orderId, LocalDateTime orderDate, String itemId, String targetState) {
      this.orderId = orderId;
      this.itemId = itemId;
      this.orderDate = orderDate;
      this.targetState = targetState;
    }

    public long getOrderId() {
      return orderId;
    }

    public LocalDateTime getOrderDate() {
      return orderDate;
    }

    public String getItemId() {
      return itemId;
    }

    public String getTargetState() {
      return targetState;
    }

  }
//Informacion de envio
  static class ShipmentInfo {
    final Order order;
    final Warehouse warehouse;
    final LocalDateTime guaranteedDeliveryDate;
    final String boxType;
    final float shippingPrice;

    ShipmentInfo(Order order, Warehouse warehouse, LocalDateTime guaranteedDeliveryDate, String boxType,
        float shippingPrice) {
      this.order = order;
      this.warehouse = warehouse;
      this.guaranteedDeliveryDate = guaranteedDeliveryDate;
      this.boxType = boxType;
      this.shippingPrice = shippingPrice;
    }

    public Order getOrder() {
      return order;
    }

    public String getItemId() {
      return order.itemId;
    }

    public Warehouse getWarehouse() {
      return warehouse;
    }

    public LocalDateTime getGuaranteedDeliveryDate() {
      return guaranteedDeliveryDate;
    }

    public String getBoxType() {
      return boxType;
    }

    public float getShippingPrice() {
      return shippingPrice;
    }

    public String toCsvLine() {
      return new StringBuilder().append(order.orderId).append(SEMICOLON).append(warehouse.toName()).append(SEMICOLON)
          .append(DATE_PATTERN.format(guaranteedDeliveryDate)).append(SEMICOLON).append(boxType).append(SEMICOLON)
          .append(DECIMAL_FORMAT.format(shippingPrice)).append(SEMICOLON)
          .append(DECIMAL_FORMAT.format(getShippingExperiencePrice())).toString();
    }

    public Float getShippingExperiencePrice() {
      long hours = order.orderDate.until(guaranteedDeliveryDate, ChronoUnit.HOURS);
      return hours * EXPERIENCE_PRICE_BY_HOUR;
    }

    public Float getTotalPrice() {
      return getShippingPrice() + getShippingExperiencePrice();
    }
  }

  static class CsvParser {

    public static final Order parseOrder(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      LocalDateTime orderDate = LocalDateTime.parse(input[1], DATE_PATTERN);
      return new Order(Long.valueOf(input[0]), orderDate, input[2], input[4]);
    }

    public static final Stock parseStock(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new Stock(input[0], Warehouse.fromName(input[1]), Integer.valueOf(input[2]));
    }

    public static final BoxType parseBoxType(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new BoxType(input[0], Integer.valueOf(input[1]), Integer.valueOf(input[2]), Integer.valueOf(input[3]),
          Integer.valueOf(input[4]), Float.valueOf(input[5].replaceAll(",", ".")));
    }

    public static final CarrierPricing parseCarrierPricings(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      String costString = input[2];
      return new CarrierPricing(Warehouse.fromName(input[0]), input[1], Float.valueOf(costString.replaceAll(",", ".")));
    }

    public static final DepartureTime parseDepartureTime(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      Warehouse warehouse = Warehouse.fromName(input[0]);

      String departureTimes[] = input[2].split(COLON);
      List<ShippingHour> shippingHours = Arrays.stream(departureTimes).map(new Function<String, ShippingHour>() {

        @Override
        public ShippingHour apply(String departureTime) {
          String[] values = departureTime.trim().split(" ");
          DayOfWeek dayOfWeek = DayOfWeek.valueOf(values[0]);
          LocalTime localTime = LocalTime.parse(values[1]);
          return new ShippingHour(dayOfWeek, localTime);
        }
      }).collect(Collectors.toList());
      return new DepartureTime(warehouse, input[1], shippingHours);
    }

    public static final CarrierTime parseCarrierTime(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new CarrierTime(Warehouse.fromName(input[0]), input[1], Integer.valueOf(input[2].split(" ")[0]));
    }

    public static final Item parseItem(String inputLine) {
      String[] input = inputLine.split(SEMICOLON);
      return new Item(input[0], Integer.valueOf(input[2]), Integer.valueOf(input[3]), Integer.valueOf(input[4]),
          Integer.valueOf(input[5]));
    }
  }
  //Gestor de envio
  
  static class ShipmentsManager {

    final Integer PACKAGE_PREPARATION_HOURS = 4;

    static class NoSuitableBoxException extends RuntimeException {
      private static final long serialVersionUID = 7513400494522133911L;

      public NoSuitableBoxException(String itemId) {
        super("There is no suitable for item " + itemId);
      }
    }

    static class NoSuitableWarehouseException extends RuntimeException {
      private static final long serialVersionUID = 7513400494522133911L;

      public NoSuitableWarehouseException(String itemId, String targetState) {
        super("There is no warehouse with stock and deliver options for item " + itemId + " and target state "
            + targetState);
      }
    }
    
    private List<Item> items;
    private List<BoxType> boxTypes;
    private List<Stock> stocks;
    private List<CarrierPricing> pricings;
    private List<CarrierTime> times;
    private List<DepartureTime> departures;

    public ShipmentsManager(List<Item> items, List<BoxType> boxTypes, List<CarrierPricing> carrierPricings,
        List<DepartureTime> departureTimes, List<CarrierTime> carrierTimes, List<Stock> initialStocks) {
      this.items = items;
      this.boxTypes = boxTypes;
      this.pricings = carrierPricings;
      this.departures = departureTimes;
      this.times = carrierTimes;
      this.stocks = initialStocks;
    }
    //Encuentra la mejor formacion de envio
    public ShipmentInfo findBestShipmentInfo(Order order) throws NoSuitableWarehouseException, NoSuitableBoxException {
      ArrayList<Warehouse> availableWarehouses = new ArrayList<>();

      // First of all we check if we have stock in all of our warehouses for the given
      // order
      for (Stock stock : this.stocks) {
        if ((stock.getItemId().equals(order.getItemId())) && (stock.getStock() > 0)) {
          availableWarehouses.add(stock.getWarehouse());
        }
        // Avoid extra iterations...
        if (availableWarehouses.size() == Warehouse.values().length)
          break;
      }

      if (availableWarehouses.size() == 0) {
        throw new NoSuitableWarehouseException(order.getItemId(), order.getTargetState());
      }

      BoxType boxType = findBestBoxType(order);
      ShipmentInfo info = findBestRoute(availableWarehouses, order, boxType);

      if (info == null)
        throw new NoSuitableWarehouseException(order.getItemId(), order.getTargetState());

      decreaseStock(info.getWarehouse(), order);

      return info;
    } 
    
   
    //Warehouses es una lista que me indica en que almacenes tengo disponible el stock de ese producto.
    private ShipmentInfo findBestRoute(List<Warehouse> warehouses, Order order, BoxType box)
        throws NoSuitableWarehouseException {
    	
    	 
    	 
    	final float volumenCaja = box.getVolume();
    	float precio = 0;
    	
    	//Lista donde se almacena las posibles soluciones
    	List<ShipmentInfo> solucionesPosibles = new ArrayList<>();
    	
    	//Lista donde se guarda los precios calculados 
    	List<Float> precios = new ArrayList<>();
    	
    	//Lista donde se guarda las unidades disponibles de ese producto
    	List<Integer> unidades = new ArrayList<>();
    	
    	int indice ;
    	for(Warehouse ware :warehouses) {
    		int unidadesDeStock = -1;
    		for(Stock caja : this.stocks ) {
    			//Guardo el valor de stock para ese almacen y para ese objeto I
    			if(caja.getWarehouse().equals(ware)&&caja.getItemId().equals(order.getItemId())) {
    				unidadesDeStock = caja.getStock();
    			}
    		}
    		// Las listas Pricings time y depurteTime tiene atributos y estructura parecida por lo que basta iterrar solo una.
    		for(indice=0; indice < this.pricings.size();indice++) {
    			
    			if(order.getTargetState().equals(this.pricings.get(indice).getTargetState())&& ware.equals(this.pricings.get(indice).getWarehouse())) {
    				List<ShippingHour> misHoras = this.departures.get(indice).getShippingHours();
    				for(ShippingHour enMisHoras: misHoras) {
    					//Calculamos la hora de entrega 
    					LocalDateTime horaLLegada = this.getDeliveryDateTime(order.getOrderDate(), enMisHoras, this.times.get(indice));
    					//Calculamos el precio de enviar ese producto a ese destino 
    					precio = volumenCaja*this.pricings.get(indice).getVolumePrice();
    					 
    					
    					solucionesPosibles.add(new ShipmentInfo(order,ware, horaLLegada, box.getBoxType(), precio));
    					
    					//Con estas dos listas no ayudaran a eleguir la solucion optima de las posibles soluciones.
    					precios.add(solucionesPosibles.get(solucionesPosibles.size()-1).getTotalPrice());
    					unidades.add(unidadesDeStock);
    				}
    			}
    		}
    		
    	}
    	//Coge el minimo valor de la lista precios.
    	float minimo = Collections.min(precios);
    	
    	//Lista donde guardaremos los indices.
    	List<Integer> indexLista = new ArrayList<>();
    	
    	//Comparamos todos los precios obtenidos y cogemos los menores de ellos.
    	for (int i=0; i<precios.size(); i++){
    		if (precios.get(i)==minimo){
    		
    			indexLista.add(i);
    		}
    		
    	}
    	//Si solamente se obtuvo una lista de un elemento significa 
    	if(indexLista.size()==1) {
    		return solucionesPosibles.get(indexLista.get(0));
    	}
    	//Si se obtuvo mas de dos elemento signfica que cuesta lo mismo enviarlo de un punto a otro y tenemos que usar el criterio de coger el almacen que tenga mayor stock
    	else {
    		int comparador = -1;
    		int indiceFinal = -1;
    		//
    		for(int indiceAux=0; indiceAux<indexLista.size(); indiceAux++) {
    			if(unidades.get(indexLista.get(indiceAux))>comparador) {
    				comparador = unidades.get(indexLista.get(indiceAux));
    				indiceFinal = indiceAux;
    			}
    		}
    		//Devolvemos de la lista solucionesPosibles el elemento optimo mediante el indiceFinal
    		return solucionesPosibles.get(indiceFinal);
    		//tendria el de mayor stock
    	}    
    }
    
    
    private BoxType findBestBoxType(Order order) throws NoSuitableBoxException {
    	//Un pedido tiene un atributo de tipo String que contiene el Identificador del Item.
    	//Almacenamos el IdDelItem
    	String itemId = order.getItemId();
    	
    	// Aqui se guardara nuestro objeto item que encontraremos en la lista de items
    	Item toBuyItem = null;
    	
    	
    	//buscamos el pedido en la lista de items
    	for (Item thing : this.items) {
    		if(itemId.equals(thing.getItemId())) {
    			//apuntamos a ese item que se corresponde con el Id dado en la lista con el puntero toBuyItem
    			
    			toBuyItem = thing;
    			break;
    		}
    	}
    	
    	//Al salir tengo que ver si ToBuyItem es null en ese caso significa que no encontro el item en la lista.
    	if(toBuyItem==null) {
    		 System.out.println("Error item no encontrado");
    	}
    	
    	//Al llegar aqui se supone que  ToBuyItem se corresponde con el item que se desea comprar es decir existe en la lista.
    	//Se empieza a comprara el pesos del objeto con el peso que permite las cajas y sus dimensiones.
    	for (BoxType box : this.boxTypes) {
    	
    		//La lista  de cajas esta ordenda por capacidad de peso.
    		//Si no cumple esta condicion cambiamos a una caja mas grande iterando la lista de cajas.
    		if( toBuyItem != null && toBuyItem.getWeight() <= box.getMaxWeight() ) {
    			//El objeto se puede rotar a diferentes posiciones para guardarlo en la caja.
    			//Comparamos las medidas de la caja con las diferentes posibles rotaciones del objeto
    			if	(
    					// 			  x  < anchoCaja 				    y <  largoCaja   			      z < altoCaja	
    					(toBuyItem.width <= box.width  && toBuyItem.length <= box.length &&  toBuyItem.height <= box.height)||
    					//             x  < anchoCaja 				      z <  largoCaja   			      y < altoCaja	
    					(toBuyItem.width  <= box.width &&  toBuyItem.height <= box.length && toBuyItem.length <= box.height)||
    					//             y  < anchoCaja 				      x <  largoCaja   			      z < altoCaja    					
    					(toBuyItem.length <= box.width &&  toBuyItem.width  <= box.length && toBuyItem.height <= box.height)||
    					//             y  < anchoCaja 				      z <  largoCaja   			      x < altoCaja 					
    					(toBuyItem.length <= box.width &&  toBuyItem.height <= box.length && toBuyItem.width  <= box.height)||
    					//             z  < anchoCaja 				      x <  largoCaja   			      y < altoCaja					
    					( toBuyItem.height <= box.width &&  toBuyItem.width  <= box.length && toBuyItem.length <= box.height)||
    					//             z  < anchoCaja 				      y <  largoCaja   			      x < altoCaja   					
    					(toBuyItem.height <= box.width &&  toBuyItem.length <= box.length &&  toBuyItem.width <= box.height)
    					
    				)
    				//si se cumple la condicion significa que entra en la caja.
    				//NOTA: FALTA CONTEMPLAR EL CASO EN QUE EL OBJETO ENTRE DE FORMA DIAGONAL.
    			{
    				
    			return box;
   
    			}
    			
    		}
    		}return null;   	
    }

    private LocalDateTime getDeliveryDateTime(LocalDateTime orderDate, ShippingHour shippingHour, CarrierTime time) {
      // From this hour we can send the order
      LocalDateTime startDate = orderDate.plusHours(PACKAGE_PREPARATION_HOURS);
      startDate = startDate.plusHours(time.getWarehouse().getTimeZoneOffset());

      // Next day we can ship the order
      LocalDateTime nextDeliveryDay = startDate.with(TemporalAdjusters.next(shippingHour.getDay()))
          .withHour(shippingHour.getTime().getHour()).withMinute(shippingHour.getTime().getMinute());

      int days = (int) startDate.until(nextDeliveryDay, ChronoUnit.DAYS);

      if (days == 7 && (nextDeliveryDay.getHour() * 60 + nextDeliveryDay.getMinute()) >= (startDate.getHour() * 60
          + startDate.getMinute())) { // We can send the day of the order
        LocalDateTime arrivalDate = startDate.withHour(shippingHour.getTime().getHour())
            .withMinute(shippingHour.getTime().getMinute()) // Today at the hour the carrier leaves
            .plusHours(time.getCarrierTime()).minusHours(time.getWarehouse().getTimeZoneOffset()); // Plus offset
        return arrivalDate;
      } else { // Send it ASAP
        return nextDeliveryDay.plusHours(time.getCarrierTime()).minusHours(time.getWarehouse().getTimeZoneOffset());
      }
    }
    
    /**
     * Overwrites our stock of the Order's item with a unit less
     */
    private void decreaseStock(Warehouse warehouse, Order order) {
      for (Stock stock : this.stocks) {
        if (stock.getWarehouse() != warehouse)
          continue;
        if (stock.getItemId().equals(order.getItemId())) {
          int s = stock.getStock();
          --s;
          this.stocks.set(this.stocks.indexOf(stock), new Stock(stock.getItemId(), warehouse, s));
          break;
        }
      }
    }
  }

  public static void main(String[] args) throws IOException {
	//Lista del stocks
    List<Stock> stocks = new ArrayList<>();
    //Lista de los tipos de cajas
    List<BoxType> boxTypes = new ArrayList<>();
    //Lista de Precios del Trasportista
    List<CarrierPricing> carrierPricings = new ArrayList<>();
    //Lista de Horario de salida
    List<DepartureTime> departureTimes = new ArrayList<>();
    //Lista de tiempos del transportista
    List<CarrierTime> carrierTimes = new ArrayList<>();
    //Lista de objeto  trasportar
    List<Item> items = new ArrayList<>();

    
    //Lista de los pedidos
    List<Order> orders = new ArrayList<>();

    Consumer<String> stockConsumer = input -> stocks.add(CsvParser.parseStock(input));
    Consumer<String> boxTypeConsumer = input -> boxTypes.add(CsvParser.parseBoxType(input));
    Consumer<String> carrierPricingConsumer = input -> carrierPricings.add(CsvParser.parseCarrierPricings(input));
    Consumer<String> departureTimeConsumer = input -> departureTimes.add(CsvParser.parseDepartureTime(input));
    Consumer<String> carrierTimeConsumer = input -> carrierTimes.add(CsvParser.parseCarrierTime(input));
    Consumer<String> itemConsumer = input -> items.add(CsvParser.parseItem(input));
    Consumer<String> orderConsumer = input -> orders.add(CsvParser.parseOrder(input));

    // BufferedWriter bw = new BufferedWriter(new
    // FileWriter(System.getenv("OUTPUT_PATH")));
    BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"));
    // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    BufferedReader br = new BufferedReader(new FileReader("input.txt"));

    String inputLine;
    Consumer<String> consumer = t -> {
    };
    while ((inputLine = br.readLine()) != null) {
      switch (inputLine) {
      case "---Orders---":
        consumer = orderConsumer;
        
        break;
      case "---Stocks---":
        consumer = stockConsumer;
        break;
      case "---BoxTypes---":
        consumer = boxTypeConsumer;
        break;
      case "---CarrierPricing---":
        consumer = carrierPricingConsumer;
        break;
      case "---DepartureTimes---":
        consumer = departureTimeConsumer;
        break;
      case "---CarrierTimes---":
        consumer = carrierTimeConsumer;
        break;
      case "---Items---":
        consumer = itemConsumer;
        break;
      default:
        consumer.accept(inputLine);
        break;
      }
    }
    br.close();
   
    Collections.sort(orders, new Comparator<Order>() {
      @Override
      public int compare(Order arg0, Order arg1) {
        return arg0.getOrderDate().compareTo(arg1.getOrderDate());
      }
    });

    ShipmentsManager shipmentsManager = new ShipmentsManager(items, boxTypes, carrierPricings, departureTimes,
        carrierTimes, stocks);

    List<ShipmentInfo> shipmentInfos = orders.stream().map(shipmentsManager::findBestShipmentInfo)
        .collect(Collectors.toList());

    Collections.sort(shipmentInfos, new Comparator<ShipmentInfo>() {
      @Override
      public int compare(ShipmentInfo arg0, ShipmentInfo arg1) {
        return arg0.getOrder().getOrderDate().compareTo(arg1.getOrder().getOrderDate());
      }
    });

    
    
    Float totalShipmentPrice = 0.0f;

    for (ShipmentInfo shipmentInfo : shipmentInfos) {
      totalShipmentPrice += shipmentInfo.shippingPrice + shipmentInfo.getShippingExperiencePrice();
    }
    StringBuilder output = new StringBuilder();
    output.append(totalShipmentPrice + "\n");
    for (ShipmentInfo shipmentInfo : shipmentInfos) {
      output.append(shipmentInfo.toCsvLine() + "\n");
    }
    bw.write(output.toString());
    bw.close();
    System.out.println("Your total shipment price is: " + totalShipmentPrice);
  };

}
