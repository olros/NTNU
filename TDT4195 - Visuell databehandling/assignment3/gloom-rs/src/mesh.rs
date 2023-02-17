use tobj;

// internal helper
fn generate_color_vec(color: [f32; 4], num: usize) -> Vec<f32> {
    color.iter().cloned().cycle().take(num*4).collect()
}

// Mesh

pub struct Mesh {
    pub vertices    : Vec<f32>,
    pub normals     : Vec<f32>,
    pub colors      : Vec<f32>,
    pub indices     : Vec<u32>,
    pub index_count : i32,
}

impl Mesh {
    pub fn from(mesh: tobj::Mesh, color: [f32; 4]) -> Self {
        let num_verts = mesh.positions.len() / 3;
        let index_count = mesh.indices.len() as i32;
        Mesh {
            vertices: mesh.positions,
            normals: mesh.normals,
            indices: mesh.indices,
            colors: generate_color_vec(color, num_verts),
            index_count,
        }
    }
}

// Lunar terrain

pub struct Terrain;
impl Terrain {
    pub fn load(path: &str) -> Mesh {
        println!("Loading terrain model...");
        let before = std::time::Instant::now();
        let (models, _materials)
            = tobj::load_obj(path,
                &tobj::LoadOptions{
                    triangulate: true,
                    single_index: true,
                    ..Default::default()
                }
            ).expect("Failed to load terrain model");
        let after = std::time::Instant::now();
        println!("Done in {:.3}ms.", after.duration_since(before).as_micros() as f32 / 1e3);

        if models.len() > 1 || models.len() == 0 {
            panic!("Please use a model with a single mesh!")
            // You could try merging the vertices and indices
            // of the separate meshes into a single mesh.
            // I'll leave that as an optional exercise. ;)
        }

        let terrain = models[0].to_owned();
        println!("Loaded {} with {} points and {} triangles.",
            terrain.name,
            terrain.mesh.positions.len() /3,
            terrain.mesh.indices.len() / 3,
        );

        Mesh::from(terrain.mesh, [1.0, 1.0, 1.0, 1.0])
    }
}


// Helicopter

pub struct Helicopter {
    pub body       : Mesh,
    pub door       : Mesh,
    pub main_rotor : Mesh,
    pub tail_rotor : Mesh,
}

// You can use square brackets to access the components of the helicopter, if you want to use loops!
use std::ops::Index;
impl Index<usize> for Helicopter {
    type Output = Mesh;
    fn index<'a>(&'a self, i: usize) -> &'a Mesh {
        match i {
            0 => &self.body,
            1 => &self.main_rotor,
            2 => &self.tail_rotor,
            3 => &self.door,
            _ => panic!("Invalid index, try [0,3]"),
        }
    }
}

impl Helicopter {
    pub fn load(path: &str) -> Self {
        println!("Loading helicopter model...");
        let before = std::time::Instant::now();
        let (models, _materials)
            = tobj::load_obj(path,
                &tobj::LoadOptions{
                    triangulate: true,
                    single_index: true,
                    ..Default::default()
                }
            ).expect("Failed to load helicopter model");
        let after = std::time::Instant::now();
        println!("Done in {:.3}ms!", after.duration_since(before).as_micros() as f32 / 1e3);

        for model in &models {
            println!("Loaded {} with {} points and {} triangles.", model.name, model.mesh.positions.len() / 3, model.mesh.indices.len() / 3);
        }

        let body_model = models.iter().find(|m| m.name == "Body_body").expect("Incorrect model file!").to_owned();
        let door_model = models.iter().find(|m| m.name == "Door_door").expect("Incorrect model file!").to_owned();
        let main_rotor_model = models.iter().find(|m| m.name == "Main_Rotor_main_rotor").expect("Incorrect model file!").to_owned();
        let tail_rotor_model = models.iter().find(|m| m.name == "Tail_Rotor_tail_rotor").expect("Incorrect model file!").to_owned();

        Helicopter {
            body:       Mesh::from(body_model.mesh,         [0.3, 0.3, 0.3, 1.0]),
            door:       Mesh::from(door_model.mesh,         [0.1, 0.1, 0.3, 1.0]),
            main_rotor: Mesh::from(main_rotor_model.mesh,   [0.3, 0.1, 0.1, 1.0]),
            tail_rotor: Mesh::from(tail_rotor_model.mesh,   [0.1, 0.3, 0.1, 1.0]),
        }
    }
}
