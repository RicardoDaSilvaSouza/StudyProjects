//
//  RSSImageViewController.swift
//  RSSAvaliacaoIntro
//
//  Created by Usuário Convidado on 23/02/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit

class RSSImageViewController: UIViewController, RSSImageSelectViewControllerDelegate {

    @IBOutlet weak var ivFruit: UIImageView!
    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    func fruitIsSelected(fruit: String) {
        self.ivFruit.image = UIImage(named: fruit)
    }

    @IBAction func tapSelectImage(sender: UIButton) {
        self.performSegueWithIdentifier("myImageToImageSelect", sender: sender)
    }
    
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        let viewController:RSSImageSelectViewController = segue.destinationViewController as! RSSImageSelectViewController
        viewController.delegate = self
    }
}
