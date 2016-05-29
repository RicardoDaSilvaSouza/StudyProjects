//
//  RSDetailViewController.swift
//  RSGuia Turistico
//
//  Created by Usuário Convidado on 12/03/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit

class RSDetailViewController: UIViewController {
    @IBOutlet weak var labelNome: UILabel!
    @IBOutlet weak var labelEndereco: UILabel!
    @IBOutlet weak var imageLocal: UIImageView!
    @IBOutlet weak var progress: UIActivityIndicatorView!
    
    var nome:String?
    var endereco:String?
    var imagem:String?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
        self.labelNome!.text = self.nome
        self.labelEndereco!.text = self.endereco
        self.progress.startAnimating()
        self.downloadImage()
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func downloadImage(){
        let urlImage:NSURL = NSURL(string: self.imagem!)!
        let imageSession = NSURLSession.sharedSession()
        let imageTask = imageSession.downloadTaskWithURL(urlImage, completionHandler: {(url:NSURL?, response: NSURLResponse?, error: NSError?) -> Void in
            if error == nil {
                if let imageData = NSData(contentsOfURL: url!){
                    dispatch_async(dispatch_get_main_queue(), {
                        self.progress.stopAnimating()
                        self.imageLocal!.image = UIImage(data: imageData)!
                    })
                }
            } else {
                print("Erro ao baixar image: \(error?.description)")
            }
        })
        imageTask.resume()
    }
}
