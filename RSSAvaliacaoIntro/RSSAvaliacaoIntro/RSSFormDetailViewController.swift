//
//  RSSFormDetailViewController.swift
//  RSSAvaliacaoIntro
//
//  Created by Usuário Convidado on 23/02/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//

import UIKit

class RSSFormDetailViewController: UIViewController {

    @IBOutlet weak var nameLabel: UILabel!
    @IBOutlet weak var emailLabel: UILabel!
    @IBOutlet weak var phoneLabel: UILabel!
    
    var theName: String!
    var theEmail: String!
    var thePhoneNumber: String!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        nameLabel.text = theName
        emailLabel.text = theEmail
        phoneLabel.text = thePhoneNumber
        // Do any additional setup after loading the view.
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    
}
