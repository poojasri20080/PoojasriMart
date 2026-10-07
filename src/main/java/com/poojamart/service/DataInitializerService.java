package com.poojamart.service;

import com.poojamart.model.Category;
import com.poojamart.model.Product;
import com.poojamart.model.User;
import com.poojamart.repository.CategoryRepository;
import com.poojamart.repository.ProductRepository;
import com.poojamart.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataInitializerService implements ApplicationRunner {

    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public DataInitializerService(UserRepository userRepository,
                                  CategoryRepository categoryRepository,
                                  ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        initUsers();
        Map<String, Category> catMap = initCategories();
        initProducts(catMap);
    }

    private void initUsers() {
        if (!userRepository.existsByEmail("admin@poojamart.com")) {
            User admin = new User("PoojaMart Admin", "admin@poojamart.com", "admin123", "ROLE_ADMIN");
            admin.setPhone("+91 9940112233");
            admin.setAddress("PoojaMart Headquarters, MG Road");
            admin.setCity("Chennai");
            admin.setState("Tamil Nadu");
            admin.setPostalCode("600001");
            userRepository.save(admin);
        }

        if (!userRepository.existsByEmail("user@poojamart.com")) {
            User user = new User("Pooja Sharma", "user@poojamart.com", "user123", "ROLE_USER");
            user.setPhone("+91 9876543210");
            user.setAddress("No. 42, Lotus Avenue, 2nd Main Road");
            user.setCity("Chennai");
            user.setState("Tamil Nadu");
            user.setPostalCode("600040");
            userRepository.save(user);
        }
    }

    private Map<String, Category> initCategories() {
        Map<String, Category> map = new HashMap<>();

        String[][] categoriesData = {
            {"Teddy Bears", "teddy-bears", "Soft, cuddly, and lovable plush teddy bears for all ages.", "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=600&auto=format&fit=crop&q=80", "🧸"},
            {"Racing Cars", "racing-cars", "High-speed die-cast, pullback and track racing toy vehicles.", "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=600&auto=format&fit=crop&q=80", "🏎️"},
            {"Remote Control Cars", "rc-cars", "Exciting 2.4GHz rechargeable drift, 4WD monster trucks, and rock crawlers.", "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=600&auto=format&fit=crop&q=80", "🎮"},
            {"Building Blocks", "building-blocks", "Creative architectural block kits, engineering bricks, and STEM sets.", "https://images.unsplash.com/photo-1585366119957-e9730b6d0f60?w=600&auto=format&fit=crop&q=80", "🧱"},
            {"Educational Toys", "educational-toys", "STEM kits, science experiment sets, robotic toys, and learning boards.", "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=600&auto=format&fit=crop&q=80", "🔬"},
            {"Puzzle Games", "puzzle-games", "Brain teasers, 1000-piece jigsaws, 3D mechanical puzzles, and Sudoku.", "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=600&auto=format&fit=crop&q=80", "🧩"},
            {"Action Figures", "action-figures", "Articulated superheroes, cyber mechas, and fantasy warriors.", "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=600&auto=format&fit=crop&q=80", "🦸"},
            {"Toy Trains", "toy-trains", "Classic electric steam engines, track circuits, metro cars, and wooden trains.", "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=600&auto=format&fit=crop&q=80", "🚂"},
            {"Baby Toys", "baby-toys", "Non-toxic silicone rattles, sensory gyms, soothing teethers, and stackers.", "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=600&auto=format&fit=crop&q=80", "👶"},
            {"Outdoor Toys", "outdoor-toys", "Foldable scooters, pop-up camping play tents, archery, and lawn games.", "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=600&auto=format&fit=crop&q=80", "⚽"}
        };

        for (String[] cat : categoriesData) {
            Category category = categoryRepository.findByNameIgnoreCase(cat[0]).orElseGet(() -> {
                Category c = new Category(cat[0], cat[1], cat[2], cat[3], cat[4]);
                return categoryRepository.save(c);
            });
            map.put(cat[0], category);
        }

        return map;
    }

    private void initProducts(Map<String, Category> catMap) {
        if (productRepository.count() >= 50) {
            return;
        }

        List<Product> products = new ArrayList<>();

        // 1. Teddy Bears (5 items)
        Category c1 = catMap.get("Teddy Bears");
        products.add(new Product(
            "Giant Plush Brown Cuddle Bear (4 Feet)",
            "Ultra-soft giant plush teddy bear crafted with premium hypoallergenic cotton. Perfect birthday gift with warm huggable feel and durable stitching.",
            1499.0, 2499.0, 35, c1,
            "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1556012018-50c5c0da73bf?w=800&auto=format&fit=crop&q=80,https://images.unsplash.com/photo-1543332164-6e82f355badc?w=800&auto=format&fit=crop&q=80",
            4.8, 420, "HugBuddy", "3+ Years",
            "Material: 100% PP Cotton Filling | Height: 120 cm | Washable: Yes | Weight: 1.8 kg",
            true, true, false
        ));
        products.add(new Product(
            "Classic Fluffy Golden Honey Bear",
            "Timeless classic honey-toned teddy bear with soft velvety paws and red satin ribbon bow. Gentle on sensitive skin.",
            699.0, 1199.0, 50, c1,
            "https://images.unsplash.com/photo-1556012018-50c5c0da73bf?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=800&auto=format&fit=crop&q=80",
            4.7, 215, "HugBuddy", "1+ Years",
            "Material: Super Soft Plush Fabric | Height: 45 cm | Care: Hand Washable",
            false, true, false
        ));
        products.add(new Product(
            "Plush Black & White Cuddle Panda",
            "Adorable realistic baby panda soft plush toy with embroidered sparkly eyes and cuddly bamboo leaf accessory.",
            799.0, 1299.0, 40, c1,
            "https://images.unsplash.com/photo-1543332164-6e82f355badc?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=800&auto=format&fit=crop&q=80",
            4.9, 310, "EcoPlush", "2+ Years",
            "Material: Organic Cotton Shell | Height: 40 cm | Non-toxic: Certified",
            true, false, true
        ));
        products.add(new Product(
            "Knitted Vintage Polar Bear Toy",
            "Hand-knitted retro winter polar bear wearing cozy knitted red scarf. Distinctive heirloom quality soft toy.",
            899.0, 1499.0, 25, c1,
            "https://images.unsplash.com/photo-1563245372-f21724e3856d?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1560743641-3914f4c4b88c?w=800&auto=format&fit=crop&q=80",
            4.6, 145, "CraftyTales", "2+ Years",
            "Material: Cotton Wool Blend | Height: 35 cm | Features: Hand-stitched Scarf",
            false, false, true
        ));
        products.add(new Product(
            "Pastel Velvet Blossom Pink Bear",
            "Gentle pastel pink teddy bear with soothing touch, perfect nursery companion and bedtime sleep snuggle buddy.",
            599.0, 999.0, 60, c1,
            "https://images.unsplash.com/photo-1560743641-3914f4c4b88c?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1559454403-b8fb88521f11?w=800&auto=format&fit=crop&q=80",
            4.5, 98, "SweetDreams", "1+ Years",
            "Material: Micro-velvet Fabric | Height: 30 cm | Color: Pastel Pink",
            false, false, false
        ));

        // 2. Racing Cars (5 items)
        Category c2 = catMap.get("Racing Cars");
        products.add(new Product(
            "Die-Cast GT Turbo Supercar 1:24 Scale",
            "Heavy alloy metal racing car featuring openable gullwing doors, realistic rubber racing tires, and pull-back high torque motor.",
            899.0, 1499.0, 45, c2,
            "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            4.8, 560, "SpeedMasters", "4+ Years",
            "Scale: 1:24 | Material: Zinc Alloy & ABS | Doors: Openable | Action: Pull-Back",
            true, true, false
        ));
        products.add(new Product(
            "Formula 1 Aerodynamic Track Racer",
            "Streamlined yellow and black Formula Grand Prix racing model with front aerodynamic wing and slick racing slicks.",
            749.0, 1200.0, 30, c2,
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=800&auto=format&fit=crop&q=80",
            4.7, 180, "ApexRacer", "5+ Years",
            "Scale: 1:32 | Material: Diecast Metal | Features: Low-profile aero chassis",
            false, true, false
        ));
        products.add(new Product(
            "Vintage Classic Alloy Speed Roadster",
            "1950s inspired metal vintage racing roadster with metallic chrome accents and working steering suspension.",
            999.0, 1699.0, 20, c2,
            "https://images.unsplash.com/photo-1581235720704-06d3acfcb36f?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1508974239320-0a029497e820?w=800&auto=format&fit=crop&q=80",
            4.9, 240, "RetroWheels", "6+ Years",
            "Scale: 1:18 | Finish: Gloss Enamel | Wheels: Free-rolling chrome spoked",
            true, false, true
        ));
        products.add(new Product(
            "High-Speed Highway Police Patrol Cruiser",
            "Police pursuit cruiser toy car with flashing emergency roof beacon lights, siren sounds, and inertia drive motor.",
            649.0, 999.0, 55, c2,
            "https://images.unsplash.com/photo-1508974239320-0a029497e820?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1594787318286-3d835c1d207f?w=800&auto=format&fit=crop&q=80",
            4.6, 310, "CitySquad", "3+ Years",
            "Power: 3x AG13 button cells (included) | Sounds: Siren, Engine roar",
            false, false, false
        ));
        products.add(new Product(
            "Track Day Turbo Drift Car with Ramp Set",
            "Twin turbo drift cars complete with dual lane launch ramp, speed curve loop, and finish line flag.",
            1199.0, 1899.0, 28, c2,
            "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            4.7, 195, "SpeedMasters", "4+ Years",
            "Contents: 2 Diecast cars, 1.2m Track & Loop | Assembly: Snap-fit track",
            false, false, true
        ));

        // 3. Remote Control Cars (5 items)
        Category c3 = catMap.get("Remote Control Cars");
        products.add(new Product(
            "4WD All-Terrain Monster Truck 2.4GHz RC",
            "Heavy-duty off-road suspension with oversized knobby tires, splash-proof electronics, and 20 km/h top speed. USB rechargeable.",
            1999.0, 3499.0, 25, c3,
            "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1563720223185-11003d516935?w=800&auto=format&fit=crop&q=80",
            4.9, 680, "TorqueRC", "6+ Years",
            "Drive: 4x4 All-Wheel Drive | Frequency: 2.4GHz (50m range) | Battery: 7.4V Li-ion",
            true, true, false
        ));
        products.add(new Product(
            "2.4GHz High-Speed Drift RC Sports Coupe",
            "Sleek sports coupe equipped with interchangeable drift slick tires and rubber grip tires, LED underglow lights, and proportional throttle.",
            1699.0, 2799.0, 32, c3,
            "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800&auto=format&fit=crop&q=80",
            4.8, 340, "SpeedDrift", "8+ Years",
            "Speed: Up to 25 km/h | Tires: 4x Drift Slicks + 4x Rubber Tires | Range: 40m",
            false, true, false
        ));
        products.add(new Product(
            "Rock Crawler 4x4 Mountain Conqueror RC",
            "Twin motor rock climbing crawler capable of conquering 45-degree rocky inclines with independent articulated shock absorbers.",
            2199.0, 3899.0, 18, c3,
            "https://images.unsplash.com/photo-1563720223185-11003d516935?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1558981403-c5f9899a28bc?w=800&auto=format&fit=crop&q=80",
            4.7, 210, "TorqueRC", "7+ Years",
            "Incline Capability: 45° | Suspension: Independent 4-link | Waterproof: IPX4",
            true, false, true
        ));
        products.add(new Product(
            "360° Stunt Tumbler & Flip RC Buggy",
            "Double-sided stunt vehicle that flips over obstacles, performs 360-degree high-speed spins, and features dazzling multi-color LED wheels.",
            1299.0, 2199.0, 45, c3,
            "https://images.unsplash.com/photo-1511919884226-fd3cad34687c?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=800&auto=format&fit=crop&q=80",
            4.6, 490, "FlipMaster", "4+ Years",
            "Stunts: 360° Spin, Double-Sided Driving | Lighting: Wheel LEDs | USB Rechargeable",
            false, false, false
        ));
        products.add(new Product(
            "Desert Trophy Sand Dune RC Buggy",
            "Aggressive cage frame desert buggy with rear-wheel drive, oil-filled shocks, and sand paddle tires for backyard racing.",
            1799.0, 2999.0, 22, c3,
            "https://images.unsplash.com/photo-1568605117036-5fe5e7bab0b7?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?w=800&auto=format&fit=crop&q=80",
            4.8, 175, "DesertKing", "6+ Years",
            "Drive: Rear 2WD High-Torque | Scale: 1:16 | Run Time: 25 mins per charge",
            false, false, true
        ));

        // 4. Building Blocks (5 items)
        Category c4 = catMap.get("Building Blocks");
        products.add(new Product(
            "Architectural Medieval Castle Building Set (850 Pcs)",
            "Detailed medieval stone castle complete with working drawbridge, watchtowers, knight minifigures, and royal treasury.",
            1899.0, 3299.0, 30, c4,
            "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1585366119957-e9730b6d0f60?w=800&auto=format&fit=crop&q=80",
            4.9, 520, "BrickForge", "8+ Years",
            "Piece Count: 850 Pieces | Material: High Grade ABS Plastic | Compatible: Yes",
            true, true, false
        ));
        products.add(new Product(
            "Mega Creative Colorful Bricks Storage Tub (600 Pcs)",
            "Vibrant multi-colored classic building blocks with windows, doors, wheels, and a sturdy storage tub with handle.",
            1099.0, 1899.0, 60, c4,
            "https://images.unsplash.com/photo-1585366119957-e9730b6d0f60?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1560169897-fc0cdbdfa4d5?w=800&auto=format&fit=crop&q=80",
            4.8, 730, "BlockVille", "4+ Years",
            "Pieces: 600 assorted pieces | Container: 15L Plastic Tub | Colors: 8 colors",
            false, true, false
        ));
        products.add(new Product(
            "City Rescue Fire Station & Engine Blocks (480 Pcs)",
            "Multi-level city fire station headquarters with extendable ladder fire truck, rescue helicopter, and 4 firefighter figures.",
            1499.0, 2499.0, 35, c4,
            "https://images.unsplash.com/photo-1532330393533-443990a51d10?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1585366119957-e9730b6d0f60?w=800&auto=format&fit=crop&q=80",
            4.7, 340, "CityHeroes", "6+ Years",
            "Pieces: 480 | Includes: Fire Truck, Helicopter, 4 Minifigures | Instructions: Full Color Book",
            true, false, false
        ));
        products.add(new Product(
            "STEM Mechanical Gears & Motorized Crane Kit",
            "Engineering building set with interlocking gears, pulleys, shafts, and battery-powered motor to construct real lifting machines.",
            1699.0, 2899.0, 25, c4,
            "https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&auto=format&fit=crop&q=80",
            4.8, 280, "GearTech", "8+ Years",
            "Motor: Electric Gear Motor (Requires 2xAA) | Models: 6-in-1 Build Options | STEM Accredited",
            false, false, true
        ));
        products.add(new Product(
            "Space Shuttle Launch Center Blocks (520 Pcs)",
            "Explore space with rocket booster stages, detachable orbit capsule, launch gantry tower, and astronaut crew.",
            1599.0, 2699.0, 28, c4,
            "https://images.unsplash.com/photo-1560169897-fc0cdbdfa4d5?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1513542789411-b6a5d4f31634?w=800&auto=format&fit=crop&q=80",
            4.9, 195, "AstroBricks", "7+ Years",
            "Pieces: 520 | Height Assembled: 42 cm | Accessories: Satellite & Lunar Rover",
            false, false, true
        ));

        // 5. Educational Toys (5 items)
        Category c5 = catMap.get("Educational Toys");
        products.add(new Product(
            "Motorized Solar System Planetarium Projector",
            "Rotating celestial planetarium displaying 8 revolving planets around an illuminated LED sun with built-in astronomy voice guide.",
            1299.0, 2199.0, 40, c5,
            "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=800&auto=format&fit=crop&q=80",
            4.9, 610, "CosmoLearn", "6+ Years",
            "Lighting: Sun Dome Projector | Audio: English Narration | Power: 3xAA Batteries",
            true, true, false
        ));
        products.add(new Product(
            "Magnetic Double-Sided Math & Alphabet Easel",
            "Wooden easel featuring magnetic whiteboard on one side and chalkboard on the other. Includes 100+ magnetic letters, numbers, and eraser.",
            999.0, 1699.0, 50, c5,
            "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=800&auto=format&fit=crop&q=80",
            4.7, 440, "BrainSpark", "3+ Years",
            "Board Size: 45 x 35 cm | Material: Natural Pine Wood | Non-toxic Chalk included",
            false, true, false
        ));
        products.add(new Product(
            "Junior Science Optical Microscope Kit (1200x)",
            "Real working student laboratory microscope with 100x, 400x, and 1200x magnification, LED lighting, prepared slides, and pipette.",
            1499.0, 2499.0, 30, c5,
            "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=800&auto=format&fit=crop&q=80",
            4.8, 380, "LabGenius", "8+ Years",
            "Magnification: 100x - 1200x | Accessories: 12 Blank & Prepared Slides, Tweezers, Vials",
            true, false, true
        ));
        products.add(new Product(
            "Talking Interactive Phonics & Spelling Tablet",
            "Touch-sensitive audio learning pad that teaches phonetic alphabet sounds, vocabulary words, spelling challenges, and nursery tunes.",
            799.0, 1299.0, 55, c5,
            "https://images.unsplash.com/photo-1516321318423-f06f85e504b3?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1503676260728-1c00da094a0b?w=800&auto=format&fit=crop&q=80",
            4.6, 290, "SmartStart", "2+ Years",
            "Display: High-contrast touch buttons | Audio: Clear native speaker accents | Modes: 6 learning modes",
            false, false, false
        ));
        products.add(new Product(
            "Programmable Smart Coding Robot with App Control",
            "Friendly coding robot equipped with obstacle avoidance sensors, path-following line sensors, and block-based coding controls.",
            2499.0, 4299.0, 15, c5,
            "https://images.unsplash.com/photo-1531482615713-2afd69097998?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1509228468518-180dd4864904?w=800&auto=format&fit=crop&q=80",
            4.9, 160, "RoboEdu", "7+ Years",
            "Connectivity: Bluetooth 5.0 | Sensors: Ultrasonic & Optical | Compatibility: Android/iOS App",
            false, false, true
        ));

        // 6. Puzzle Games (5 items)
        Category c6 = catMap.get("Puzzle Games");
        products.add(new Product(
            "1000-Piece Illustrated World Map Jigsaw Puzzle",
            "Premium matte-finish 1000-piece puzzle showing detailed geographic landmarks, wild animals, and ocean wonders with zero glare.",
            699.0, 1199.0, 40, c6,
            "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1591991731833-b4807cf7ef94?w=800&auto=format&fit=crop&q=80",
            4.8, 510, "PuzzleCrafter", "10+ Years",
            "Pieces: 1000 Precision Cut | Finished Dimensions: 70 x 50 cm | Material: Recycled Blue Board",
            true, true, false
        ));
        products.add(new Product(
            "3D Wooden Mechanical Pendulum Clock Puzzle",
            "Laser-cut birch plywood puzzle that assembles without glue into a real functioning pendulum escapement clock.",
            1599.0, 2799.0, 20, c6,
            "https://images.unsplash.com/photo-1591991731833-b4807cf7ef94?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=800&auto=format&fit=crop&q=80",
            4.9, 390, "TimberCraft", "12+ Years",
            "Assembly Time: 4-5 Hours | Pieces: 170 Laser Cut Parts | Tool Free Assembly",
            true, false, true
        ));
        products.add(new Product(
            "Magnetic Smooth Speed Cube 3x3",
            "Competition-grade speed cube featuring internal magnetic positioning, anti-pop mechanism, and frosted stickerless surfaces.",
            499.0, 899.0, 70, c6,
            "https://images.unsplash.com/photo-1589829085413-56de8ae18c73?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1618336753974-aae8e04506aa?w=800&auto=format&fit=crop&q=80",
            4.9, 820, "SpeedSpin", "6+ Years",
            "Type: 3x3 Magnetic | Weight: 78g | Surface: Matte Stickerless ABS Plastic",
            false, true, false
        ));
        products.add(new Product(
            "Wooden Brain Teaser Tangram & Pattern Block Board",
            "Montessori geometric wooden puzzle with 60 pattern guide cards to boost spatial reasoning and creative problem solving.",
            549.0, 899.0, 50, c6,
            "https://images.unsplash.com/photo-1587654780291-39c9404d746b?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=800&auto=format&fit=crop&q=80",
            4.6, 230, "WoodPuzzler", "4+ Years",
            "Pieces: 155 Solid Wood Shapes | Cards: 60 Dual-sided Challenge Cards | Water-based Non-toxic Paint",
            false, false, false
        ));
        products.add(new Product(
            "Deluxe Magnetic Sudoku & Chess 2-in-1 Board",
            "Double-sided wooden game console with magnetic numbered tiles for Sudoku puzzles and travel magnetic chess on reverse.",
            899.0, 1499.0, 30, c6,
            "https://images.unsplash.com/photo-1606326608606-aa0b62935f2b?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1591991731833-b4807cf7ef94?w=800&auto=format&fit=crop&q=80",
            4.7, 160, "ClassicMind", "7+ Years",
            "Board Size: 30 x 30 cm | Includes: 100 Sudoku tiles + 32 Chess pieces | Travel pouch included",
            false, false, true
        ));

        // 7. Action Figures (5 items)
        Category c7 = catMap.get("Action Figures");
        products.add(new Product(
            "Galactic Armored Superhero Figure (12 Inch)",
            "Articulated superhero figure with 16 points of articulation, removable energy shield, photon blaster, and light-up chest emblem.",
            999.0, 1699.0, 45, c7,
            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
            4.8, 470, "TitanForce", "4+ Years",
            "Height: 30 cm | Articulation: 16 Joint Points | Battery: 2x LR44 included",
            true, true, false
        ));
        products.add(new Product(
            "Cyber Mecha Titan Robot Defender",
            "Heavy mecha armored titan warrior equipped with shoulder-mounted missile pods and heavy dual plasma blades.",
            1199.0, 1999.0, 30, c7,
            "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80",
            4.9, 310, "MechaForge", "6+ Years",
            "Height: 25 cm | Accessories: 2 Swords, 4 Missile Projectiles | Material: Heavy ABS",
            true, false, false
        ));
        products.add(new Product(
            "Mythic Dragon Knight with Armored Steed",
            "Medieval fantasy knight in gleaming silver armor mounted on an articulated battle dragon with flapping wings.",
            1399.0, 2299.0, 25, c7,
            "https://images.unsplash.com/photo-1568832359672-e36cf5d74f54?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80",
            4.7, 190, "LegendRealm", "5+ Years",
            "Figure Height: 18 cm | Dragon Wingspan: 38 cm | Material: Non-toxic PVC",
            false, false, true
        ));
        products.add(new Product(
            "Deep Space Planetary Explorer Astronaut",
            "Realistic NASA-inspired astronaut figure with transparent visor helmet, tether cable, life-support pack, and lunar flag.",
            849.0, 1399.0, 35, c7,
            "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1607604276583-eef5d076aa5f?w=800&auto=format&fit=crop&q=80",
            4.8, 250, "AstroQuest", "4+ Years",
            "Height: 20 cm | Visor: Retractable Gold Tint | Accessories: Moon rock sample container",
            false, true, false
        ));
        products.add(new Product(
            "Shadow Blade Martial Samurai Warrior",
            "Ancient legendary samurai action figure with dual katana swords, traditional battle armor, and decorative presentation stand.",
            949.0, 1599.0, 20, c7,
            "https://images.unsplash.com/photo-1579783900882-c0d3dad7b119?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1563089145-599997674d42?w=800&auto=format&fit=crop&q=80",
            4.6, 140, "HonorLegends", "8+ Years",
            "Height: 18 cm | Weapons: 2 Alloy Die-cast Katanas | Display Base included",
            false, false, true
        ));

        // 8. Toy Trains (5 items)
        Category c8 = catMap.get("Toy Trains");
        products.add(new Product(
            "Electric Classic Steam Locomotive with Real Smoke & Sounds",
            "Authentic retro steam train featuring working front headlamp, realistic chug sounds, passenger coaches, and real non-toxic steam smoke effect.",
            1999.0, 3299.0, 28, c8,
            "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1520038410233-7141be7e6f97?w=800&auto=format&fit=crop&q=80",
            4.9, 740, "IronRail", "4+ Years",
            "Track Length: 3.5m Oval Track | Smoke Effect: Water-based Mist | Lights: Warm LED Headlight",
            true, true, false
        ));
        products.add(new Product(
            "High-Speed Electric Bullet Express Train Set",
            "Modern aerodynamic bullet train with 3 motorized passenger carriages, elevated rail bridge pillars, and automated station switch.",
            1699.0, 2799.0, 35, c8,
            "https://images.unsplash.com/photo-1520038410233-7141be7e6f97?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=800&auto=format&fit=crop&q=80",
            4.8, 380, "ApexRail", "3+ Years",
            "Carriages: 3 Interlinked Cars | Track: 4.2m Loop with Bridge | Battery: 2xAA",
            false, true, false
        ));
        products.add(new Product(
            "Natural Hardwood Magnetic Railway & Bridge (60 Pcs)",
            "Solid beech wood magnetic train set with bridge viaduct, cargo crane, trees, traffic signs, and compatible wooden tracks.",
            1499.0, 2499.0, 40, c8,
            "https://images.unsplash.com/photo-1532330393533-443990a51d10?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=800&auto=format&fit=crop&q=80",
            4.9, 410, "TimberTracks", "3+ Years",
            "Material: 100% Solid European Beech Wood | Connections: Embedded Strong Magnets | Pieces: 60",
            true, false, true
        ));
        products.add(new Product(
            "Industrial Freight Cargo Train with Gantry Crane",
            "Heavy cargo diesel engine with container flats, oil tanker car, and magnetic cargo loading gantry station.",
            1399.0, 2199.0, 30, c8,
            "https://images.unsplash.com/photo-1518709268805-4e9042af9f23?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1578328819058-b69f3a3b0f6b?w=800&auto=format&fit=crop&q=80",
            4.7, 260, "CargoLine", "4+ Years",
            "Includes: Diesel Engine + 3 Freight Wagons + Crane | Track: 3m Layout",
            false, false, false
        ));
        products.add(new Product(
            "City Metro Rapid Transit Rail Play System",
            "Double-ended metro train with opening passenger doors, platform announcer audio, and illuminated tunnel segment.",
            1299.0, 2099.0, 32, c8,
            "https://images.unsplash.com/photo-1508974239320-0a029497e820?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1520038410233-7141be7e6f97?w=800&auto=format&fit=crop&q=80",
            4.6, 185, "MetroMotion", "3+ Years",
            "Audio: 4 Station Announcement Sounds | Lighting: Interior Coach LEDs | Easy Snap Rails",
            false, false, true
        ));

        // 9. Baby Toys (5 items)
        Category c9 = catMap.get("Baby Toys");
        products.add(new Product(
            "Food-Grade Silicone Teething Rattle Set (8 Pcs)",
            "100% BPA-free food-grade silicone rattle set with textured teething surfaces, soft pastel chime beads, and steam sterilizer case.",
            699.0, 1199.0, 50, c9,
            "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=800&auto=format&fit=crop&q=80",
            4.9, 620, "TinySnuggles", "0+ Months",
            "Safety: BPA, Phthalate & PVC Free | Sterilization: Boil safe up to 100°C | Case included",
            true, true, false
        ));
        products.add(new Product(
            "Rainbow Sensory Stacking & Nesting Ring Tower",
            "Classic developmental stacking tower with 7 colorful graduated textured rings and whimsical swirl base.",
            449.0, 799.0, 65, c9,
            "https://images.unsplash.com/photo-1596461404969-9ae70f2830c1?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=800&auto=format&fit=crop&q=80",
            4.8, 540, "PlaySprout", "6+ Months",
            "Height: 28 cm | Rings: 7 Distinct Colors & Textures | Easy Grip Design",
            false, true, false
        ));
        products.add(new Product(
            "Padded Musical Kick & Play Activity Gym",
            "Extra-plush padded floor mat featuring a kick-activated musical piano, overhead arch with hanging mirrors, and tactile sensory toys.",
            1799.0, 2999.0, 22, c9,
            "https://images.unsplash.com/photo-1584824486509-112e4181ff6b?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop&q=80",
            4.9, 390, "TinySnuggles", "0+ Months",
            "Dimensions: 85 x 65 cm | Mat: Machine Washable | Piano: 3 Musical Modes & Lullabies",
            true, false, true
        ));
        products.add(new Product(
            "Soft Crinkle Fabric Sensory Cloth Books (4 Pack)",
            "Tear-resistant chewable soft fabric cloth books featuring 3D animal tails, squeaker buttons, and high-contrast crinkle pages.",
            599.0, 999.0, 48, c9,
            "https://images.unsplash.com/photo-1544816155-12df9643f363?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1515488042361-ee00e0ddd4e4?w=800&auto=format&fit=crop&q=80",
            4.7, 310, "SoftPages", "3+ Months",
            "Quantity: 4 Cloth Books (Jungle, Farm, Ocean, Numbers) | Washable: 100% Machine Washable",
            false, false, false
        ));
        products.add(new Product(
            "Interactive Animal Sounds Musical Piano",
            "Chunky easy-press infant keyboard that plays cheerful animal sounds, cheerful baby nursery melodies, and gentle flashing lights.",
            699.0, 1199.0, 38, c9,
            "https://images.unsplash.com/photo-1560743641-3914f4c4b88c?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1584824486509-112e4181ff6b?w=800&auto=format&fit=crop&q=80",
            4.6, 220, "PlaySprout", "9+ Months",
            "Keys: 8 Notes + 5 Animal Keys | Volume: High/Low Adjustable Volume Control",
            false, false, true
        ));

        // 10. Outdoor Toys (5 items)
        Category c10 = catMap.get("Outdoor Toys");
        products.add(new Product(
            "Foldable 3-Wheel LED Light-Up Kids Kick Scooter",
            "Adjustable height kick scooter with lean-to-steer technology, flashing multi-color kinetic LED wheels, and rear fender foot brake.",
            1599.0, 2799.0, 35, c10,
            "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=800&auto=format&fit=crop&q=80",
            4.9, 810, "ScootJoy", "3-8 Years",
            "Handlebar Height: 65 to 80 cm (3 Stages) | Weight Capacity: 50 kg | Wheels: ABEC-7 Bearings",
            true, true, false
        ));
        products.add(new Product(
            "Kids Pop-Up Castle Adventure Play Tent & Tunnel",
            "Easy instant pop-up castle tent with crawl-through tube tunnel and basketball hoop ball pit. Folds into compact zipper carry bag.",
            1299.0, 2199.0, 30, c10,
            "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=800&auto=format&fit=crop&q=80",
            4.8, 490, "WonderDen", "2+ Years",
            "Dimensions: Castle 105x135cm, Tunnel 120cm | Fabric: Breathable 190T Polyester | Indoor/Outdoor",
            false, true, false
        ));
        products.add(new Product(
            "Kids Safe Archery Bow & Suction Arrow Target Set",
            "Ergonomic recurve bow with LED glow sighting light, 6 safe rubber suction cup arrows, quiver with strap, and standing target board.",
            899.0, 1499.0, 45, c10,
            "https://images.unsplash.com/photo-1516627145497-ae6968895b74?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1543852786-1cf6624b9987?w=800&auto=format&fit=crop&q=80",
            4.7, 360, "ArrowPro", "6+ Years",
            "Range: Up to 15 meters | Bow Length: 65 cm | Target Stand: 45 cm Diameter",
            true, false, true
        ));
        products.add(new Product(
            "Aerodynamic Foam Hand-Launch Glider Planes (2 Pack)",
            "Durable EPP high-polymer foam stunt airplanes that perform loops, long-distance gliding over 30 meters, and LED nighttime flight mode.",
            499.0, 899.0, 60, c10,
            "https://images.unsplash.com/photo-1543852786-1cf6624b9987?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1517649763962-0c623266ddc0?w=800&auto=format&fit=crop&q=80",
            4.6, 410, "SkyFlyer", "4+ Years",
            "Wingspan: 48 cm | Wings: Dual Tail Positions for Stunt Loop / Level Flight | Pack: 2 Planes",
            false, false, false
        ));
        products.add(new Product(
            "Lawn Bowling Skittles Outdoor Family Game",
            "Set of 10 solid wooden pins with painted numbers and 2 weighted bowling balls for fun garden, beach, and backyard games.",
            799.0, 1399.0, 35, c10,
            "https://images.unsplash.com/photo-1534447677768-be436bb09401?w=800&auto=format&fit=crop&q=80",
            "https://images.unsplash.com/photo-1526778548025-fa2f459cd5c1?w=800&auto=format&fit=crop&q=80",
            4.7, 220, "GardenPlay", "4+ Years",
            "Pins: 10 Solid Pine Pins (20cm) | Balls: 2 Wooden Balls (8cm) | Carry Canvas Bag Included",
            false, false, true
        ));

        productRepository.saveAll(products);
        System.out.println("Initialized " + products.size() + " realistic toy products across 10 categories!");
    }
}
