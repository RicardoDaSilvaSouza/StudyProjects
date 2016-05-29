//
//  Ponto+CoreDataProperties.swift
//  RSGuia Turistico
//
//  Created by Usuário Convidado on 12/03/16.
//  Copyright © 2016 Usuário Convidado. All rights reserved.
//
//  Choose "Create NSManagedObject Subclass…" from the Core Data editor menu
//  to delete and recreate this implementation file for your updated model.
//

import Foundation
import CoreData

extension Ponto {

    @NSManaged var nome: String?
    @NSManaged var endereco: String?
    @NSManaged var imagem: String?
    @NSManaged var lat: NSNumber?
    @NSManaged var lon: NSNumber?

}
