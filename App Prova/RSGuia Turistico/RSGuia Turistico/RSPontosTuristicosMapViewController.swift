//
//  RSPontosTuristicosMapViewController.swift
//  RSGuia Turistico
//
//  Created by Usuário Convidado on 12/03/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit
import MapKit
import CoreData

class RSPontosTuristicosMapViewController: UIViewController, MKMapViewDelegate, NSFetchedResultsControllerDelegate {
    @IBOutlet weak var mapView: MKMapView!
    
    let locationManager:CLLocationManager = CLLocationManager()
    let urlPontos:NSURL = NSURL(string: "http://flameworks.com.br/fiap/pontosTuristicos.txt")!
    let defaultLocation:CLLocationCoordinate2D = CLLocationCoordinate2DMake(-23.550303, -46.634184)
    var fetchedResultController:NSFetchedResultsController = NSFetchedResultsController()
    var session:NSURLSession?
    var managedObjectContext:NSManagedObjectContext?


    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view
        self.setupCoreData()
        self.locationManager.requestWhenInUseAuthorization()
        
        self.mapView.showsUserLocation = true
        self.mapView.region = MKCoordinateRegionMakeWithDistance(self.defaultLocation, 1200, 1200)
        
        if NSUserDefaults.standardUserDefaults().objectForKey("loaded") == nil {
            self.session = NSURLSession(configuration: NSURLSessionConfiguration.defaultSessionConfiguration())
            
            let task:NSURLSessionDataTask = self.session!.dataTaskWithURL(self.urlPontos, completionHandler: {(data: NSData?, response: NSURLResponse?, error: NSError?) -> Void in
                if error == nil {
                    let arrPontos:[RSPonto] = self.getPontosFromJson(data!)
                    if self.insertPontos(arrPontos) {
                        NSUserDefaults.standardUserDefaults().setValue("S", forKey: "loaded")
                        NSUserDefaults.standardUserDefaults().synchronize()
                    }
                    let arrAnnotations:[RSCustomAnnotation] = self.loadAnnotations(arrPontos)
                    dispatch_async(dispatch_get_main_queue(), {
                        self.mapView.addAnnotations(arrAnnotations)
                    })
                } else {
                    print("Erro ao obter o JSON: \(error?.description)")
                }
            })
            
            task.resume()
        } else {
            self.getFetchedResultController()
            let arrPontos:[RSPonto] = self.getPontosFromBd()
            let arrAnnotations:[RSCustomAnnotation] = self.loadAnnotations(arrPontos)
            dispatch_async(dispatch_get_main_queue(), {
                self.mapView.addAnnotations(arrAnnotations)
            })
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
        let annotationView:MKAnnotationView = sender as! MKAnnotationView
        let annotation:RSCustomAnnotation = annotationView.annotation as! RSCustomAnnotation
        let viewController:RSDetailViewController = segue.destinationViewController as! RSDetailViewController
        viewController.nome = annotation.ponto!.nome
        viewController.endereco = annotation.ponto!.endereco
        viewController.imagem = annotation.ponto!.imagem
    }
    
    func mapView(mapView: MKMapView, viewForAnnotation annotation: MKAnnotation) -> MKAnnotationView? {
        if annotation is RSCustomAnnotation {
            var result: MKAnnotationView?
            let reuseId = "reuseCustomAnnotation"
            result = mapView.dequeueReusableAnnotationViewWithIdentifier(reuseId)
            if result == nil {
                result = MKAnnotationView(annotation: annotation, reuseIdentifier: reuseId)
                result!.image = UIImage(named: "bluePin")
                result!.canShowCallout = true
                result!.rightCalloutAccessoryView = UIButton(type: UIButtonType.DetailDisclosure)
            }
            return result
        }
        return nil
    }
    
    func mapView(mapView: MKMapView, annotationView view: MKAnnotationView, calloutAccessoryControlTapped control: UIControl) {
        self.performSegueWithIdentifier("segueDetail", sender: view)
    }
    
    func getPontosFromJson(data: NSData?) -> [RSPonto]{
        var arrPontos:[RSPonto] = []
        do {
            let json = try NSJSONSerialization.JSONObjectWithData(data!, options: [])
            if let locais:[[String:AnyObject]] = json["locais"] as? [[String:AnyObject]]{
                for local:[String:AnyObject] in locais {
                    let ponto:RSPonto = RSPonto()
                    ponto.nome = local["nome"] as? String
                    ponto.endereco = local["endereco"] as? String
                    ponto.imagem = local["imagem"] as? String
                    if let coordenadas:[String:AnyObject] = local["coordenadas"] as? [String:AnyObject] {
                        ponto.lat = coordenadas["lat"] as? Double
                        ponto.lon = coordenadas["lon"] as? Double
                    }
                    arrPontos.append(ponto)
                }
            }
        } catch {
            print("Ocorreu um erro ao converter o JSON!")
        }
        return arrPontos
    }
    
    func loadAnnotations(arrPontos:[RSPonto]) -> [RSCustomAnnotation]{
        var arrAnnotations:[RSCustomAnnotation] = []
        for ponto:RSPonto in arrPontos {
            let annotation:RSCustomAnnotation = RSCustomAnnotation(coordinate:             CLLocationCoordinate2D(latitude: ponto.lat!, longitude: ponto.lon!), title: ponto.nome!, subtitle: ponto.endereco, ponto: ponto)
            arrAnnotations.append(annotation)
        }
        return arrAnnotations
    }
    
    func setupCoreData(){
        let modelUrl:NSURL? = NSBundle.mainBundle().URLForResource("PontoTuristicoModel", withExtension: "momd")
        let model:NSManagedObjectModel = NSManagedObjectModel(contentsOfURL: modelUrl!)!
        
        let coordinator:NSPersistentStoreCoordinator = NSPersistentStoreCoordinator(managedObjectModel: model)
        
        let paths = NSSearchPathForDirectoriesInDomains(NSSearchPathDirectory.DocumentDirectory, NSSearchPathDomainMask.UserDomainMask, true)
        let docPath:NSURL = NSURL(fileURLWithPath: paths[0])
        let sqlitePath:NSURL = docPath.URLByAppendingPathComponent("PontoTuristicoModel.sqlite")
        
        do{
            try coordinator.addPersistentStoreWithType(NSSQLiteStoreType, configuration: nil, URL: sqlitePath, options: nil)
        }catch{
            print("Erro ao associar coordinator")
        }
        
        self.managedObjectContext = NSManagedObjectContext(concurrencyType:         NSManagedObjectContextConcurrencyType.MainQueueConcurrencyType)
        self.managedObjectContext!.persistentStoreCoordinator = coordinator
    }
    
    func insertPontos(arrPontos:[RSPonto]) -> Bool{
        for ponto:RSPonto in arrPontos {
            let entityDescription = NSEntityDescription.entityForName("Ponto", inManagedObjectContext: self.managedObjectContext!)
            
            let mangedPonto = Ponto(entity: entityDescription!, insertIntoManagedObjectContext: self.managedObjectContext!)
            
            mangedPonto.nome = ponto.nome
            mangedPonto.endereco = ponto.endereco
            mangedPonto.imagem = ponto.imagem
            mangedPonto.lat = ponto.lat
            mangedPonto.lon = ponto.lon
        }
        
        do{
            try self.managedObjectContext?.save()
            return true
        } catch {
            print("Erro ao inserir ponto na base.")
        }
        return false
    }
    
    func getFetchedResultController(){
        let fetchRequest:NSFetchRequest = NSFetchRequest(entityName: "Ponto")
        
        let sortDescriptor:NSSortDescriptor = NSSortDescriptor(key: "nome", ascending: true)
        fetchRequest.sortDescriptors = [sortDescriptor]
        
        self.fetchedResultController = NSFetchedResultsController(fetchRequest: fetchRequest, managedObjectContext: self.managedObjectContext!, sectionNameKeyPath: nil, cacheName: nil)
        
        self.fetchedResultController.delegate = self
        
        do{
            try self.fetchedResultController.performFetch()
        } catch {
            print("Erro ao tentar buscar pontos na base.")
        }
    }
    
    func getPontosFromBd() -> [RSPonto]{
        var arrPontos:[RSPonto] = []
        
        for local in self.fetchedResultController.fetchedObjects as! [Ponto] {
            let ponto:RSPonto = RSPonto()
            
            ponto.nome = local.nome
            ponto.endereco = local.endereco
            ponto.imagem = local.imagem
            ponto.lat = local.lat as? Double
            ponto.lon = local.lon as? Double
            
            arrPontos.append(ponto)
        }
        return arrPontos
    }
}