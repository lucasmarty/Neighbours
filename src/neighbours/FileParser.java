package neighbours;

import java.io.*;

public class FileParser {
	
	public static void readFile(String path) throws IOException {
		FileInputStream fin = new FileInputStream(path);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(fin));
		String line = null;
		
		while ((line = br.readLine()) != null) {
			if (line.trim().equals(""))
				continue;
			
			String[] exploded = line.split(":");
			
			if (exploded.length != 2)
				throw new IllegalArgumentException("Error in file syntax " + line);

			if (exploded[0].trim().equals("grid"))
			{
				String[] dim = exploded[1].split(",");
				
				if (dim.length != 2)
					throw new IllegalArgumentException("Error in file syntax "+ line);
				
				MainContext.instance().setWidth(Integer.parseInt(dim[0].trim()));
				MainContext.instance().setHeight(Integer.parseInt(dim[1].trim()));
			}
			else if (exploded[0].trim().equals("zone"))
			{
				String[] param = exploded[1].split(",");
				
				if (param.length < 5)
					throw new IllegalArgumentException("Error in file syntax "+ line);
				
				int orig_x = Integer.parseInt(param[1].trim());
				int orig_y = Integer.parseInt(param[2].trim());
				int nb_building = Integer.parseInt(param[3].trim());
				int capa = Integer.parseInt(param[4].trim());
				
				if (param[0].trim().equals("office"))
				{
					BuildingZone<Office> zone = new BuildingZone<>(orig_x, orig_y, nb_building, Office.class, capa);
					MainContext.instance().add_office_zone(zone);
				}
				else if (param[0].trim().equals("shop"))
				{
					BuildingZone<Shop> zone = new BuildingZone<>(orig_x, orig_y, nb_building, Shop.class, capa);
					MainContext.instance().add_shop_zone(zone);
				}
				else if (param[0].trim().equals("cinema"))
				{
					BuildingZone<MovieTheater> zone = new BuildingZone<>(orig_x, orig_y, nb_building, MovieTheater.class, capa);
					MainContext.instance().add_movie_zone(zone);
				}
				else if (param[0].trim().equals("house"))
				{
					if (param.length != 6)
						throw new IllegalArgumentException("Error in file syntax "+ line);
					int nbHuman = Integer.parseInt(param[5].trim());
					BuildingZone<House> zone = new BuildingZone<>(orig_x, orig_y, nb_building, House.class, capa);
					MainContext.instance().add_house_zone(zone, nbHuman);
				}
				else
					throw new IllegalArgumentException("Error in file syntax: " + line);
			}
			else
				throw new IllegalArgumentException("Error in file syntax: " + line);
		}
		
		br.close();
	}

}
