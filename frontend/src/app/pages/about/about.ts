import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

interface TeamMember {
  name: string;
  position: string;
  description: string;
  email: string;
  phone: string;
  image: string;
}

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [
    CommonModule,
    MatCardModule,
    MatIconModule
  ],
  templateUrl: './about.html',
  styleUrls: ['./about.scss']
})
export class AboutComponent {
  readonly teamMembers: TeamMember[] = [
    {
      name: 'Peter Gabathuler',
      position: 'Geschäftsführung',
      description: 'Peter hatte die Idee für diese Plattform. Er führt RhyCraft, und bringt das Wissen aus der Baubranche mit.',
      email: 'peter.gabathuler@bau-platform.ch',
      phone: '+41 44 123 45 67',
      image: 'https://ui-avatars.com/api/?name=Peter+Gabathuler&size=400&background=64748b&color=ffffff&bold=true'
    },
    {
      name: 'Lukas Lehmann',
      position: 'Produktleiter',
      description: 'Lukas weiss wie digitale Firmen aufgebaut sind. Er führt Online-Plattformen zum Erfolg.',
      email: 'lukas.lehmann@bau-platform.ch',
      phone: '+41 44 123 45 68',
      image: 'https://ui-avatars.com/api/?name=Lukas+Lehmann&size=400&background=6b7280&color=ffffff&bold=true'
    },
    {
      name: 'Andreas Sprecher',
      position: 'Programmierer',
      description: 'Andreas programmiert die Plattform. Er bringt das technische Know-how mit.',
      email: 'andreas.sprecher@bau-platform.ch',
      phone: '+41 44 123 45 69',
      image: 'https://ui-avatars.com/api/?name=Andreas+Sprecher&size=400&background=78716c&color=ffffff&bold=true'
    },
    {
      name: 'Damian Bernegger',
      position: 'Holzbauexperte',
      description: 'Damian führt Bernegger Holzbau und bringt das praktische Wissen aus dem Holzbau mit. Er weiss, was die Betriebe wirklich brauchen.',
      email: 'damian.bernegger@bau-platform.ch',
      phone: '+41 44 123 45 70',
      image: 'https://ui-avatars.com/api/?name=Damian+Bernegger&size=400&background=57534e&color=ffffff&bold=true'
    }
  ];

  // Contact email to avoid @ symbol issues in template
  readonly contactEmail = 'info@bau-platform.ch';
  readonly contactEmailDisplay = 'info' + String.fromCharCode(64) + 'bau-platform.ch';
}
